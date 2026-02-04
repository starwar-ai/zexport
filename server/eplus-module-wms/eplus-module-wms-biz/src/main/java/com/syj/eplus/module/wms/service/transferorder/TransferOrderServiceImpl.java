package com.syj.eplus.module.wms.service.transferorder;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.syj.eplus.framework.common.dict.CalculationDict;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.framework.common.enums.TransferOrderStatusEnum;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import com.syj.eplus.module.wms.controller.admin.transferorder.vo.TransferOrderPageReqVO;
import com.syj.eplus.module.wms.controller.admin.transferorder.vo.TransferOrderRespVO;
import com.syj.eplus.module.wms.controller.admin.transferorder.vo.TransferOrderSaveReqVO;
import com.syj.eplus.module.wms.convert.transferorder.TransferOrderConvert;
import com.syj.eplus.module.wms.dal.dataobject.bill.BillItemDO;
import com.syj.eplus.module.wms.dal.dataobject.stock.StockDO;
import com.syj.eplus.module.wms.dal.dataobject.transferorder.TransferOrderDO;
import com.syj.eplus.module.wms.dal.dataobject.transferorderitem.TransferOrderItem;
import com.syj.eplus.module.wms.dal.mysql.bill.BillItemMapper;
import com.syj.eplus.module.wms.dal.mysql.transferorder.TransferOrderMapper;
import com.syj.eplus.module.wms.dal.mysql.transferorderitem.TransferOrderItemMapper;
import com.syj.eplus.module.wms.enums.StockSourceTypeEnum;
import com.syj.eplus.module.wms.enums.StockTypeEnum;
import com.syj.eplus.module.wms.service.bill.BillService;
import com.syj.eplus.module.wms.service.stock.StockService;
import com.syj.eplus.module.wms.util.BatchCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.wms.enums.ErrorCodeConstants.*;

/**
 * 调拨 Service 实现类
 *
 * @author du
 */
@Service
@Validated
public class TransferOrderServiceImpl implements TransferOrderService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private TransferOrderMapper transferOrderMapper;
    @Resource
    private CodeGeneratorApi codeGeneratorApi;
    @Resource
    private BillService billService;
    @Resource
    private BatchCodeUtil batchCodeUtil;
    @Resource
    private TransferOrderItemMapper transferOrderItemMapper;
    @Resource
    private StockService stockService;
    @Resource
    private BillItemMapper billItemMapper;
    private static final String SN_TYPE = "TransferOrder";
    private static final String CODE_PREFIX = "DB";


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTransferOrder(TransferOrderSaveReqVO createReqVO) {
        TransferOrderDO transferOrder = TransferOrderConvert.INSTANCE.convertTransferOrderDO(createReqVO);
        // 生成 调拨 编号
        transferOrder.setCode(codeGeneratorApi.getCodeGenerator(SN_TYPE, CODE_PREFIX));
        transferOrder.setStatus(TransferOrderStatusEnum.PENDING_SUBMIT.getStatus());
        // 插入
        transferOrderMapper.insert(transferOrder);
        List<TransferOrderItem> children = createReqVO.getChildren();
        if (CollUtil.isEmpty(children)) {
            return transferOrder.getId();
        }
        children.forEach(s -> {
            s.setId(null);
            s.setTransferOrderId(transferOrder.getId());
        });
        transferOrderItemMapper.insertBatch(children);
        Integer submitFlag = createReqVO.getSubmitFlag();
        if (BooleanEnum.YES.getValue().equals(submitFlag)) {
            dealStock(children, transferOrder);
            TransferOrderDO transferOrderDO = new TransferOrderDO().setStatus(TransferOrderStatusEnum.DONE.getStatus()).setId(transferOrder.getId());
            transferOrderMapper.updateById(transferOrderDO);
        }
        // 返回
        return transferOrder.getId();
    }


    /**
     * 处理库存
     *
     * @param transferOrderItemList 调拨明细
     * @param transferOrder         调拨单
     */
    private void dealStock(List<TransferOrderItem> transferOrderItemList, TransferOrderDO transferOrder) {
        // 转换出库明细 batchCodeUtil为空则不需要生成批次号
        List<BillItemDO> outBillItemDOList = TransferOrderConvert.INSTANCE.convertBillItemDOList(transferOrderItemList, StockTypeEnum.OUT_STOCK.getValue(), null, transferOrder);
        // 出库
        dealOutbound(transferOrder.getId(), transferOrder.getCode(), outBillItemDOList);
        // 转换出库明细
        List<BillItemDO> inBillItemDOList = TransferOrderConvert.INSTANCE.convertBillItemDOList(transferOrderItemList, StockTypeEnum.IN_STOCK.getValue(), batchCodeUtil, transferOrder);
        // 入库
        dealInBound(inBillItemDOList, transferOrder);
    }

    /**
     * 处理入库逻辑
     *
     * @param inBillItemDOList 入库明细列表
     * @param transferOrder    调拨单
     */
    private void dealInBound(List<BillItemDO> inBillItemDOList, TransferOrderDO transferOrder) {
        billService.updateBillItemList(transferOrder.getId(),transferOrder.getCode(), StockSourceTypeEnum.ALLOCATION.getValue(), inBillItemDOList,false);
        List<StockDO> stockDOList = inBillItemDOList.stream().map(s -> {
            StockDO stockDO = BeanUtils.toBean(s, StockDO.class);
            Integer actQuantity = s.getOrderQuantity();
            stockDO.setId(null).setCreator(null).setCreateTime(null).setUpdater(null).setUpdateTime(null);
            stockDO.setBillItemId(s.getId()).setBatchCode(s.getBatchCode()).setReceiptTime(LocalDateTime.now());
            stockDO.setInitQuantity(actQuantity).setAvailableQuantity(actQuantity).setLockQuantity(BigDecimal.ZERO.intValue());
            stockDO.setSaleContractCode(transferOrder.getSaleContractCode());
            return stockDO;
        }).toList();
        stockService.saveBatch(stockDOList);
    }

    /**
     * 处理出库逻辑
     *
     * @param transferOrderId 调拨单主键
     * @param billItemDOList  出库明细列表
     */
    private void dealOutbound(Long transferOrderId,String transferOrderCode, List<BillItemDO> billItemDOList) {
        // 出库
        billService.updateBillItemList(transferOrderId,transferOrderCode, StockSourceTypeEnum.ALLOCATION.getValue(), billItemDOList,false);
        // 更新批次库存及位置
        List<String> batchCodeList = billItemDOList.stream().map(BillItemDO::getBatchCode).distinct().toList();
        Map<String, StockDO> stockDOMap = stockService.getStockListByBatchCode(batchCodeList);
        if (CollUtil.isEmpty(stockDOMap)) {
            throw exception(STOCK_NOT_EXISTS_BY_BATCH_CODE, batchCodeList);
        }
        // 插入库存明细
        billItemMapper.insertBatch(billItemDOList);
        List<StockDO> stockDOList = new ArrayList<>();
        billItemDOList.forEach(s -> {
            StockDO stockDO = stockDOMap.get(s.getBatchCode());
            if (Objects.isNull(stockDO)) {
                throw exception(STOCK_NOT_EXISTS_BY_BATCH_CODE, s.getBatchCode());
            }
            // 原有可用库存
            Integer availableQuantity = stockDO.getAvailableQuantity();
            // 调拨数量
            Integer actQuantity = s.getActQuantity();
            // 库存数量
            Integer usedQuantity = stockDO.getUsedQuantity();
            if (actQuantity.compareTo(availableQuantity) > CalculationDict.ZERO) {
                throw exception(STOCK_NOT_ENOUGH);
            }
            // 可用库存 = 可用库存 - 调拨数量
            stockDO.setAvailableQuantity(availableQuantity - actQuantity);
            // 出库数量 = 出库数量 + 调拨数量
            stockDO.setUsedQuantity(usedQuantity + actQuantity);
            stockDOList.add(stockDO);
        });
        List<StockDO> stockDOS = stockService.calculateRemainTotalAmount(stockDOList);
        stockService.updateBatchById(stockDOS);
    }

    @Override
    public void updateTransferOrder(TransferOrderSaveReqVO updateReqVO) {
        // 校验存在
        validateTransferOrderExists(updateReqVO.getId());
        // 更新
        TransferOrderDO updateObj = TransferOrderConvert.INSTANCE.convertTransferOrderDO(updateReqVO);
        transferOrderMapper.updateById(updateObj);
        List<TransferOrderItem> children = updateReqVO.getChildren();
        if (CollUtil.isEmpty(children)) {
            throw exception(TRANSFER_ORDER_ITEM_NOT_EXISTS, updateObj.getCode());
        }
        transferOrderItemMapper.delete(TransferOrderItem::getTransferOrderId, updateObj.getId());
        children.forEach(s -> {
            s.setId(null);
        });
        transferOrderItemMapper.insertBatch(children);
        if (BooleanEnum.YES.getValue().equals(updateReqVO.getSubmitFlag())) {
            dealStock(children, updateObj);
            TransferOrderDO transferOrderDO = new TransferOrderDO().setStatus(TransferOrderStatusEnum.DONE.getStatus()).setId(updateReqVO.getId());
            transferOrderMapper.updateById(transferOrderDO);
        }
    }

    @Override
    public void deleteTransferOrder(Long id) {
        // 校验存在
        validateTransferOrderExists(id);
        // 删除
        transferOrderMapper.deleteById(id);
    }

    private void validateTransferOrderExists(Long id) {
        if (transferOrderMapper.selectById(id) == null) {
            throw exception(TRANSFER_ORDER_NOT_EXISTS);
        }
    }

    @Override
    public TransferOrderRespVO getTransferOrder(Long id) {
        TransferOrderDO transferOrderDO = transferOrderMapper.selectById(id);
        if (transferOrderDO == null) {
            return null;
        }
        List<TransferOrderItem> transferOrderItems = transferOrderItemMapper.selectList(TransferOrderItem::getTransferOrderId, id);
        TransferOrderRespVO transferOrderRespVO = TransferOrderConvert.INSTANCE.convertTransferOrderRespVO(transferOrderDO);
        transferOrderRespVO.setTransferOrderItemList(transferOrderItems);
        return transferOrderRespVO;
    }

    @Override
    public PageResult<TransferOrderDO> getTransferOrderPage(TransferOrderPageReqVO pageReqVO) {
        return transferOrderMapper.selectPage(pageReqVO);
    }

    @Override
    public void submitTransferOrder(Long id) {
        TransferOrderDO transferOrderDO = transferOrderMapper.selectById(id);
        if (Objects.isNull(transferOrderDO)) {
            throw exception(TRANSFER_ORDER_NOT_EXISTS);
        }
        List<TransferOrderItem> transferOrderItems = transferOrderItemMapper.selectList(TransferOrderItem::getTransferOrderId, id);
        if (CollUtil.isEmpty(transferOrderItems)) {
            throw exception(TRANSFER_ORDER_ITEM_NOT_EXISTS, transferOrderDO.getCode());
        }
        // 处理仓库信息
        dealStock(transferOrderItems, transferOrderDO);
        TransferOrderDO updateObj = new TransferOrderDO().setStatus(TransferOrderStatusEnum.DONE.getStatus()).setId(id);
        transferOrderMapper.updateById(updateObj);
    }

}
