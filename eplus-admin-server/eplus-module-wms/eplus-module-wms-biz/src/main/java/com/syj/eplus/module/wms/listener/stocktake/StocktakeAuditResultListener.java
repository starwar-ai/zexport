package com.syj.eplus.module.wms.listener.stocktake;

import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.framework.common.enums.OutStockSourceTypeEnum;
import com.syj.eplus.module.wms.api.stock.dto.BillItemSaveReqVO;
import com.syj.eplus.module.wms.api.stock.dto.BillSaveReqVO;
import com.syj.eplus.module.wms.controller.admin.adjustment.vo.AdjustmentSaveReqVO;
import com.syj.eplus.module.wms.controller.admin.adjustmentitem.vo.AdjustmentItemSaveReqVO;
import com.syj.eplus.module.wms.controller.admin.stocktake.vo.StocktakeDetailReq;
import com.syj.eplus.module.wms.controller.admin.stocktake.vo.StocktakeRespVO;
import com.syj.eplus.module.wms.controller.admin.stocktakeitem.vo.StocktakeItemRespVO;
import com.syj.eplus.module.wms.dal.dataobject.stock.StockDO;
import com.syj.eplus.module.wms.enums.StockBillStatusEnum;
import com.syj.eplus.module.wms.enums.StockSourceTypeEnum;
import com.syj.eplus.module.wms.enums.StockTypeEnum;
import com.syj.eplus.module.wms.enums.StocktakeResultEnum;
import com.syj.eplus.module.wms.service.adjustment.AdjustmentService;
import com.syj.eplus.module.wms.service.bill.BillItemService;
import com.syj.eplus.module.wms.service.bill.BillServiceImpl;
import com.syj.eplus.module.wms.service.stock.StockService;
import com.syj.eplus.module.wms.service.stocktake.StocktakeItemService;
import com.syj.eplus.module.wms.service.stocktake.StocktakeService;
import com.syj.eplus.module.wms.util.BatchCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@Slf4j
@Component
public class StocktakeAuditResultListener extends BpmProcessInstanceResultEventListener {
    @Resource
    private StocktakeService stocktakeService;
    @Resource
    private StocktakeItemService stocktakeItemService;
    @Resource
    private AdjustmentService adjustmentService;
    @Resource
    private BatchCodeUtil batchCodeUtil;
    @Resource
    private StockService stockService;
    @Resource
    private BillItemService billItemService;
    @Autowired
    private BillServiceImpl billService;

    @Override
    protected String getProcessDefinitionKey() {
        return stocktakeService.getProcessDefinitionKey();
    }

    @Override
    protected void onEvent(BpmProcessInstanceResultEvent event) {
        long id = Long.parseLong(event.getBusinessKey());
        Integer result = event.getResult();
        stocktakeService.updateAuditStatus(id, result);
        // 审核通过
        if (result.intValue() == BpmProcessInstanceResultEnum.APPROVE.getResult().intValue()) {
            StocktakeRespVO stocktake = stocktakeService.getStocktake(new StocktakeDetailReq().setStocktakeId(id));
            log.info("盘点单审核通过,信息:{}", JSONUtil.toJsonStr(stocktake));

            List<StocktakeItemRespVO> stocktakeItemRespVOList = stocktake.getStocktakeItemRespVOList();
            if (CollectionUtils.isEmpty(stocktakeItemRespVOList)) {
                log.error("盘点单{}，明细数据为空",stocktake.getCode());
                return;
            }
            List<StockDO> stockDOList = new ArrayList<>();
            // 1. 生成盘盈单&盘亏单
            Map<Integer, List<StocktakeItemRespVO>> collect = stocktakeItemRespVOList.stream().collect(Collectors.groupingBy(StocktakeItemRespVO::getStocktakeStockResult));
            // 盘盈单
            List<StocktakeItemRespVO> surplusRespVOS = collect.get(StocktakeResultEnum.SURPLUS.getValue());
            if(!CollectionUtils.isEmpty(surplusRespVOS)){
                AdjustmentSaveReqVO adjustmentSaveReqVO = BeanUtils.toBean(stocktake, AdjustmentSaveReqVO.class);
                String code = batchCodeUtil.genBatchCode("SP");
                adjustmentSaveReqVO.setId(null).setStocktakeId(id).setCode(code).setAdjustmentType(StocktakeResultEnum.SURPLUS.getValue());
                adjustmentSaveReqVO.setStocktakeCode(stocktake.getCode()).setStocktakeUserId(stocktake.getStocktakeUserId()).setStocktakeUserName(stocktake.getStocktakeUserName());
                List<AdjustmentItemSaveReqVO> adjustmentItemDOList = BeanUtils.toBean(surplusRespVOS, AdjustmentItemSaveReqVO.class);
                adjustmentSaveReqVO.setAdjustmentItemList(adjustmentItemDOList);
                adjustmentSaveReqVO.setAdjustmentDate(LocalDateTime.now()).setRemark(null);
                adjustmentSaveReqVO.setPrintFlag(BooleanEnum.NO.getValue()).setPrintTimes(BigDecimal.ZERO.intValue());
                adjustmentService.createAdjustment(adjustmentSaveReqVO);

                // 保存盘盈记录
                List<BillItemSaveReqVO> billItemSaveReqVOList = new ArrayList<>();
                BillSaveReqVO billSaveReqVO = BeanUtils.toBean(stocktake, BillSaveReqVO.class);
                billSaveReqVO.setBillTime(LocalDateTime.now());
                billSaveReqVO.setBillType(StockTypeEnum.IN_STOCK.getValue());
                billSaveReqVO.setBillStatus(StockBillStatusEnum.CONFIRMED.getValue());
                billSaveReqVO.setSourceType(OutStockSourceTypeEnum.DEFICIT.getType());
                billSaveReqVO.setSourceId(stocktake.getId());
                billSaveReqVO.setSourceCode(stocktake.getCode());
                AtomicInteger sortNum = new AtomicInteger();
                surplusRespVOS.forEach(x -> {
                    int quantity = Math.abs(x.getDiffQuantity());
                    BillItemSaveReqVO billItemSaveReqVO = BeanUtils.toBean(x, BillItemSaveReqVO.class);
//                    billItemSaveReqVO.setSourceId(id);
                    billItemSaveReqVO.setSourceType(StockSourceTypeEnum.STOCK_SURPLUS.getValue());
                    billItemSaveReqVO.setBillType(StockTypeEnum.IN_STOCK.getValue());
                    billItemSaveReqVO.setActQuantity(quantity);
                    billItemSaveReqVO.setOrderQuantity(quantity);
                    billItemSaveReqVO.setPurchaseContractCode(stocktake.getPurchaseContractCode());
                    billItemSaveReqVO.setPurchaseContractId(stocktake.getPurchaseContractId());
                    billItemSaveReqVO.setId(null);
                    billItemSaveReqVO.setSortNum(sortNum.incrementAndGet());
                    billItemSaveReqVO.setAbnormalRemark(x.getDiffReasons());
                    int boxQuantity = Math.abs(x.getStockBoxQuantity() - x.getStocktakeStockBoxQuantity());
                    billItemSaveReqVO.setActBoxQuantity(boxQuantity);
                    billItemSaveReqVO.setOrderBoxQuantity(boxQuantity);
                    billItemSaveReqVO.setPurchaseContractCode(x.getPurchaseContractCode());
                    UserDept purchaseUser = x.getPurchaseUser();
                    if (Objects.nonNull(purchaseUser)){
                        billItemSaveReqVO.setPurchaserId(purchaseUser.getUserId());
                        billItemSaveReqVO.setPurchaserDeptId(purchaseUser.getDeptId());
                    }
                    billItemSaveReqVOList.add(billItemSaveReqVO);
                });
                billSaveReqVO.setBillItemSaveReqVOList(billItemSaveReqVOList);
                billService.createBill(billSaveReqVO);

                surplusRespVOS.forEach(o -> {
                    LambdaQueryWrapperX<StockDO> queryWrapper = new LambdaQueryWrapperX<StockDO>().eqIfPresent(StockDO::getBatchCode, o.getBatchCode());
                    StockDO stockDO = stockService.getOne(queryWrapper);
                    stockDO.setAvailableQuantity(o.getStocktakeStockQuantity());
                    stockDO.setInitQuantity(stockDO.getInitQuantity()+o.getDiffQuantity());
                    stockDO.setPosition(o.getStocktakePosition());
                    stockDO.setDiffQuantity(o.getDiffQuantity());
                    stockDOList.add(stockDO);
                });
            }

            // 盘亏单
            List<StocktakeItemRespVO> deficitRespVOS = collect.get(StocktakeResultEnum.DEFICIT.getValue());
            if(!CollectionUtils.isEmpty(deficitRespVOS)){
                AdjustmentSaveReqVO adjustmentSaveReqVO = BeanUtils.toBean(stocktake, AdjustmentSaveReqVO.class);
                String code = batchCodeUtil.genBatchCode("DI");
                adjustmentSaveReqVO.setId(null).setStocktakeId(id).setCode(code).setAdjustmentType(StocktakeResultEnum.DEFICIT.getValue());
                adjustmentSaveReqVO.setStocktakeCode(stocktake.getCode()).setStocktakeUserId(stocktake.getStocktakeUserId()).setStocktakeUserName(stocktake.getStocktakeUserName());
                List<AdjustmentItemSaveReqVO> adjustmentItemDOList = BeanUtils.toBean(deficitRespVOS, AdjustmentItemSaveReqVO.class);
                adjustmentSaveReqVO.setAdjustmentItemList(adjustmentItemDOList);
                adjustmentSaveReqVO.setAdjustmentDate(LocalDateTime.now()).setRemark(null);
                adjustmentSaveReqVO.setPrintFlag(BooleanEnum.NO.getValue()).setPrintTimes(BigDecimal.ZERO.intValue());
                adjustmentService.createAdjustment(adjustmentSaveReqVO);

                // 保存盘亏记录
                List<BillItemSaveReqVO> billItemSaveReqVOList = new ArrayList<>();
                BillSaveReqVO billSaveReqVO = BeanUtils.toBean(stocktake, BillSaveReqVO.class);
                billSaveReqVO.setBillTime(LocalDateTime.now());
                billSaveReqVO.setBillType(StockTypeEnum.OUT_STOCK.getValue());
                billSaveReqVO.setBillStatus(StockBillStatusEnum.CONFIRMED.getValue());
                billSaveReqVO.setSourceType(OutStockSourceTypeEnum.DEFICIT.getType());
                billSaveReqVO.setSourceId(stocktake.getId());
                billSaveReqVO.setSourceCode(stocktake.getCode());
                AtomicInteger sortNum = new AtomicInteger();
                deficitRespVOS.forEach(x -> {
                    int quantity = Math.abs(x.getDiffQuantity());
                    BillItemSaveReqVO billItemSaveReqVO = BeanUtils.toBean(x, BillItemSaveReqVO.class);
//                    billItemSaveReqVO.setSourceId(id);
                    billItemSaveReqVO.setSourceType(StockSourceTypeEnum.STOCK_LOSS.getValue());
                    billItemSaveReqVO.setBillType(StockTypeEnum.OUT_STOCK.getValue());
                    billItemSaveReqVO.setActQuantity(quantity);
                    billItemSaveReqVO.setOrderQuantity(quantity);
                    billItemSaveReqVO.setPurchaseContractCode(stocktake.getPurchaseContractCode());
                    billItemSaveReqVO.setPurchaseContractId(stocktake.getPurchaseContractId());
                    billItemSaveReqVO.setId(null);
                    billItemSaveReqVO.setSortNum(sortNum.incrementAndGet());
                    billItemSaveReqVO.setAbnormalRemark(x.getDiffReasons());
                    int boxQuantity = Math.abs(x.getStockBoxQuantity() - x.getStocktakeStockBoxQuantity());
                    billItemSaveReqVO.setActBoxQuantity(boxQuantity);
                    billItemSaveReqVO.setOrderBoxQuantity(boxQuantity);
                    billItemSaveReqVO.setPurchaseContractCode(x.getPurchaseContractCode());
                    UserDept purchaseUser = x.getPurchaseUser();
                    if (Objects.nonNull(purchaseUser)){
                        billItemSaveReqVO.setPurchaserId(purchaseUser.getUserId());
                        billItemSaveReqVO.setPurchaserDeptId(purchaseUser.getDeptId());
                    }
                    billItemSaveReqVOList.add(billItemSaveReqVO);
                });
                billSaveReqVO.setBillItemSaveReqVOList(billItemSaveReqVOList);
                billService.createBill(billSaveReqVO);

                deficitRespVOS.forEach(o -> {
                    LambdaQueryWrapperX<StockDO> queryWrapper = new LambdaQueryWrapperX<StockDO>().eqIfPresent(StockDO::getBatchCode, o.getBatchCode());
                    StockDO stockDO = stockService.getOne(queryWrapper);
                    stockDO.setAvailableQuantity(o.getStocktakeStockQuantity());
                    stockDO.setUsedQuantity(stockDO.getUsedQuantity()+Math.abs(o.getDiffQuantity()));
                    stockDO.setPosition(o.getStocktakePosition());
                    stockDO.setDiffQuantity(o.getDiffQuantity());
                    stockDOList.add(stockDO);
                });
            }
            // 计算剩余总金额
            List<StockDO> stockDOS = stockService.calculateRemainTotalAmount(stockDOList);
            // 2. 更新批次库存及位置
            stockService.updateBatchById(stockDOS);

        }
    }
}
