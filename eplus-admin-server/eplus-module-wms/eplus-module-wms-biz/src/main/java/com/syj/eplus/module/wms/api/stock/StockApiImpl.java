package com.syj.eplus.module.wms.api.stock;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.dict.CommonDict;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.api.dept.DeptApi;
import cn.iocoder.yudao.module.system.api.dept.dto.DeptRespDTO;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.syj.eplus.framework.common.entity.*;
import com.syj.eplus.framework.common.util.CalcSpecificationUtil;
import com.syj.eplus.framework.common.util.NumUtil;
import com.syj.eplus.module.pms.api.sku.SkuApi;
import com.syj.eplus.module.wms.api.stock.dto.*;
import com.syj.eplus.module.wms.controller.admin.bill.vo.BillRespVO;
import com.syj.eplus.module.wms.convert.stock.StockConvert;
import com.syj.eplus.module.wms.dal.dataobject.bill.BillItemDO;
import com.syj.eplus.module.wms.dal.dataobject.stock.StockDO;
import com.syj.eplus.module.wms.dal.dataobject.stocklock.StockLockDO;
import com.syj.eplus.module.wms.dal.mysql.stock.StockMapper;
import com.syj.eplus.module.wms.dal.mysql.stocklock.StockLockMapper;
import com.syj.eplus.module.wms.enums.StockBillStatusEnum;
import com.syj.eplus.module.wms.enums.StockLockSourceTypeEnum;
import com.syj.eplus.module.wms.enums.StockTypeEnum;
import com.syj.eplus.module.wms.service.bill.BillService;
import com.syj.eplus.module.wms.service.stock.StockService;
import com.syj.eplus.module.wms.service.stockNotice.StockNoticeService;
import com.syj.eplus.module.wms.service.stocklock.StockLockService;
import com.syj.eplus.module.wms.util.BatchCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.dms.enums.ErrorCodeConstants.PRODUCING_QUANTITY_NOT_ENOUGH;
import static com.syj.eplus.module.wms.enums.ErrorCodeConstants.*;

/**
 * Desc—— 库存 API 实现类
 * Create by Rangers at  2024-07-09 15:27
 */
@Slf4j
@Service
public class StockApiImpl implements IStockApi {

    @Resource
    private BillService billService;
    @Resource
    private StockService stockService;
    @Resource
    private BatchCodeUtil batchCodeUtil;
    @Resource
    private StockLockService stockLockService;

    @Resource
    private SkuApi skuApi;
    @Resource
    private StockMapper stockMapper;
    @Resource
    private StockLockMapper stockLockMapper;
    @Resource
    private StockNoticeService stockNoticeService;
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private DeptApi deptApi;

    @Override
    public Boolean afterCreatePurchaseOrder(BillSaveReqVO billSaveReqVO) {
        log.info("采购合同下单-库存处理,请求参数：{}", JSONUtil.toJsonStr(billSaveReqVO));
        // 0. 校验参数
        List<BillItemSaveReqVO> billItemSaveReqVOList = billSaveReqVO.getBillItemSaveReqVOList();
        if (CollectionUtils.isEmpty(billItemSaveReqVOList)) {
            return false;
        }

        // 1. 创建单据&明细
        billSaveReqVO.setId(null).setCode(null).setRemark("");
        billSaveReqVO.setBillTime(LocalDateTime.now());
        billSaveReqVO.getBillItemSaveReqVOList().stream().forEach(x -> {
            // 入库操作设置批次号
            x.setBatchCode(batchCodeUtil.genBatchCode(CommonDict.EMPTY_STR));
        });
        Long billId = billService.createBill(billSaveReqVO).getId();
        boolean saveFlag = billId > 0;

        // 2. 处理库存批次信息
        BillRespVO billRespVO = billService.getBill(billId);
        List<BillItemDO> billItemDOList = BeanUtils.toBean(billRespVO.getChildren(), BillItemDO.class);

        Map<Long, BillItemDO> billItemDOMap = billItemDOList.stream().collect(Collectors.toMap(BillItemDO::getSkuId, Function.identity()));
        List<StockDO> stockDOList = BeanUtils.toBean(billItemDOList, StockDO.class);
        // 入库操作更新库存
        Integer defaultQuantity = BigDecimal.ZERO.intValue();

        List<Long> purchaserIdList = billItemDOList.stream().map(BillItemDO::getPurchaserId).distinct().toList();
        Map<Long, AdminUserRespDTO> purchaserMap = adminUserApi.getUserMap(purchaserIdList);
        List<Long> purchaserDeptIdList = billItemDOList.stream().map(BillItemDO::getPurchaserDeptId).distinct().toList();
        Map<Long, DeptRespDTO> purchaserDeptMap = deptApi.getDeptMap(purchaserDeptIdList);

        stockDOList.stream().forEach(stockDO -> {
            BillItemDO billItemDO = billItemDOMap.get(stockDO.getSkuId());
            stockDO.setId(null).setCreator(null).setCreateTime(null).setUpdater(null).setUpdateTime(null);
            stockDO.setBillId(billId).setBillItemId(billItemDO.getId()).setReceiptTime(LocalDateTime.now());
            stockDO.setPurchaseContractId(billItemDO.getPurchaseContractId()).setPurchaseContractCode(billItemDO.getPurchaseContractCode());
            Integer actQuantity = billItemDO.getActQuantity();
            stockDO.setInitQuantity(actQuantity).setUsedQuantity(defaultQuantity)
                    .setProducingQuantity(actQuantity).setLockQuantity(defaultQuantity);
            // 库存设置销售合同 ，已用库存为在制库存数量，可用库存为 0，库存已被销售合同占用
            stockDO.setAvailableQuantity(defaultQuantity);
            stockDO.setSaleContractId(billRespVO.getSaleContractId()).setSaleContractCode(billRespVO.getSaleContractCode());
            UserDept userDept = new UserDept();
            if (billItemDO.getPurchaserId() != null) {
                AdminUserRespDTO adminUserRespDTO = purchaserMap.get(billItemDO.getPurchaserId());
                userDept.setUserId(adminUserRespDTO.getId());
                userDept.setUserCode(adminUserRespDTO.getCode());
                userDept.setNickname(adminUserRespDTO.getNickname());
                userDept.setMobile(adminUserRespDTO.getMobile());
            }
            if (billItemDO.getPurchaserDeptId() != null) {
                DeptRespDTO deptRespDTO = purchaserDeptMap.get(billItemDO.getPurchaserDeptId());
                if(Objects.nonNull(deptRespDTO)){
                    userDept.setDeptId(deptRespDTO.getId());
                    userDept.setDeptName(deptRespDTO.getName());
                }
            }
            stockDO.setPurchaseUser(userDept);
        });
        boolean stockFlag = stockService.saveBatch(stockService.updateTotalAmount(stockDOList));
        return saveFlag && stockFlag;
    }

    @Override
    public Boolean afterCanclePurchaseOrder(Long purchaseContractId) {
        // 1. 删除入库单、入库单明细
        boolean billFlag = billService.deleteBillByContract(purchaseContractId);
        // 2. 删除库存信息
        boolean stockFlag = stockService.deleteStockByContractId(purchaseContractId);
        // 3. 删除锁定表
        stockLockService.deleteByContractId(purchaseContractId);
        return billFlag && stockFlag;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchLockStock(List<StockLockSaveReqVO> stockLockSaveReqVOList) {
        Boolean result = false;
        if (!CollectionUtils.isEmpty(stockLockSaveReqVOList)) {
            AtomicInteger atomicInteger = new AtomicInteger(0);
            stockLockSaveReqVOList.forEach(x -> {
                StockLockSourceTypeEnum stockLockSourceTypeEnum = EnumUtil.likeValueOf(StockLockSourceTypeEnum.class, x.getSourceOrderType());
                x.setRemark(stockLockSourceTypeEnum.getDesc() + " 单据号码:" + x.getSaleContractCode() + ",占用" + x.getLockQuantity() + "库存");
                Long stockLockId = stockLockService.createStockLock(x);
                if (stockLockId > 0) {
                    atomicInteger.incrementAndGet();
                }
            });
            if (atomicInteger.get() == stockLockSaveReqVOList.size()) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public Boolean cancelStockLock(String saleContractCode, Collection<Long> itemIdList, Integer sourceOrderType) {
        LambdaQueryWrapper<StockLockDO> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isBlank(saleContractCode)) {
            return false;
        }
        queryWrapper.eq(StockLockDO::getSaleContractCode, saleContractCode);
        if (CollUtil.isNotEmpty(itemIdList)) {
            queryWrapper.in(StockLockDO::getSaleContractItemId, itemIdList);
        }
        List<StockLockDO> stockLockDOList = stockLockService.list(queryWrapper);
        if (!CollectionUtils.isEmpty(stockLockDOList)) {
            AtomicInteger flagAto = new AtomicInteger(0);
            stockLockDOList.forEach(x -> {
                Boolean canceled = stockLockService.cancel(x.getId());
                if (canceled) {
                    flagAto.getAndAdd(1);
                }
            });
            return flagAto.get() == stockLockDOList.size();
        }
        return false;
    }

    @Override
    public Boolean completePurchaseOrder(Long purchaseContractId, Map<String, Integer> usedQuantityMap,Boolean domesticSaleFlag,List<String> batchCodeList) {
        List<StockDO> stockDOList;
        if (CollUtil.isEmpty(usedQuantityMap)) {
            LambdaQueryWrapperX<StockDO> queryWrapperX = new LambdaQueryWrapperX<>();
            if (Objects.nonNull(purchaseContractId)){
                queryWrapperX.eq(StockDO::getPurchaseContractId, purchaseContractId);
            }else {
                queryWrapperX.in(StockDO::getBatchCode,batchCodeList);
            }
            stockDOList = stockMapper.selectList(queryWrapperX);
        } else {
            LambdaQueryWrapperX<StockDO> queryWrapperX = new LambdaQueryWrapperX<StockDO>().in(StockDO::getBatchCode, usedQuantityMap.keySet());
            // 内销自动生产完成不需要查找采购合同  因为是挪用库存
            if (Objects.isNull(domesticSaleFlag)||!domesticSaleFlag){
                queryWrapperX.eq(StockDO::getPurchaseContractId, purchaseContractId);
            }
            stockDOList = stockMapper.selectList(queryWrapperX);
        }

        if (CollectionUtils.isEmpty(stockDOList)) {
            return false;
        }
        // 批次信息在制库存转为入库库存
        stockDOList.forEach(stockDO -> {
            Integer realUsedQuantity = 0;
            int producingQuantity = stockDO.getProducingQuantity();
            int availableQuantity = stockDO.getAvailableQuantity();
            if (CollUtil.isNotEmpty(usedQuantityMap)) {
                realUsedQuantity = usedQuantityMap.get(stockDO.getBatchCode());
            } else {
                realUsedQuantity = stockDO.getProducingQuantity();
            }
            if (Objects.isNull(realUsedQuantity) || realUsedQuantity == 0) {
                return;
            }
            // 优先使用可用数量，可用数量不足再进行生产完成
            int lockQuantity = Objects.isNull(stockDO.getLockQuantity()) ? 0 : stockDO.getLockQuantity();
            // 真实可用数量 = 可用数量 - 锁定数量
            int realAvailableQuantity = availableQuantity-lockQuantity;
            if (realAvailableQuantity >= realUsedQuantity){
                producingQuantity -= realUsedQuantity;
                availableQuantity += realUsedQuantity;
            }else if (realAvailableQuantity > 0){
                int unUsedAvaicableQuantity = realUsedQuantity - realAvailableQuantity;
                producingQuantity -= unUsedAvaicableQuantity;
                availableQuantity +=unUsedAvaicableQuantity;
            }else { // 全部使用在制数量
                producingQuantity -= realUsedQuantity;
                availableQuantity += realUsedQuantity;
            }
            stockDO.setProducingQuantity(producingQuantity);
            stockDO.setAvailableQuantity(availableQuantity);
        });
        boolean allCompleted = stockDOList.stream().filter(s -> s.getProducingQuantity() > 0).toList().isEmpty();
        boolean updated = stockMapper.updateBatch(stockDOList);
        return allCompleted && updated;
    }

    @Override
    public Map<Long, List<StockDTO>> getStockBySkuIdList(List<Long> skuIdList) {
        List<StockDTO> stockBySkuCodeList = stockLockService.getStockDTOListBySkuIdList(skuIdList);
        if (CollUtil.isEmpty(stockBySkuCodeList)) {
            return null;
        }
        List<StockDTO> dtoList = BeanUtils.toBean(stockBySkuCodeList, StockDTO.class);
        return dtoList.stream().collect(Collectors.groupingBy(StockDTO::getSkuId));
    }

    @Override
    public List<StockDTO> getStockListBySkuCode(List<String> skuCodeList) {
        return stockLockService.getStockListBySkuCode(skuCodeList);
    }

    @Override
    public List<StockDetailRespVO> listLockStock(QueryStockReqVO queryStockReqVO) {
        return BeanUtils.toBean(stockService.listBatch(queryStockReqVO), StockDetailRespVO.class);
    }

    @Override
    public List<StockLockRespVO> listStockLock(QueryStockReqVO queryStockReqVO) {
        LambdaQueryWrapper<StockLockDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StockLockDO::getSaleContractCode, queryStockReqVO.getSaleContractCode());
        List<StockLockDO> stockLockDOList = stockLockService.list(queryWrapper);
        return BeanUtils.toBean(stockLockDOList, StockLockRespVO.class);
    }

    @Override
    public List<StockLockRespVO> getStockLockRespVOBySaleContractCodes(List<String> saleContractCodes) {
        LambdaQueryWrapper<StockLockDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(StockLockDO::getSaleContractCode, saleContractCodes);
        List<StockLockDO> stockLockDOList = stockLockService.list(queryWrapper);
        return BeanUtils.toBean(stockLockDOList, StockLockRespVO.class);
    }

    @Override
    public Map<String, Integer> queryTotalStock(List<QueryStockReqVO> queryStockReqVOList) {
        return stockService.queryTotalStock(queryStockReqVOList);
    }

    public List<StockDTO> getStockDTOBySaleContractCodes(List<String> saleContractCodes) {
        List<StockDO> stockDOList = stockMapper.selectList(StockDO::getSaleContractCode, saleContractCodes);
        if (CollUtil.isEmpty(stockDOList)) {
            return new ArrayList<>();
        }
        return StockConvert.INSTANCE.convertStockDTOList(stockDOList);
    }

    @Override
    public List<StockDTO> getStockDTOBySaleContractCodesAndLastCompanyIds(List<String> saleContractCodes, List<Long> lastCompanyIds) {
        LambdaQueryWrapper<StockDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(StockDO::getSaleContractCode, saleContractCodes);
        queryWrapper.in(StockDO::getCompanyId, lastCompanyIds);
        List<StockDO> stockDOList = stockService.list(queryWrapper);
        if (CollUtil.isEmpty(stockDOList)) {
            return List.of();
        }
        return StockConvert.INSTANCE.convertStockDTOList(stockDOList);
    }

    @Override
    public List<StockDTO> getStockDTOByIdList(List<Long> idList) {
        if (CollUtil.isEmpty(idList)) {
            return List.of();
        }
        List<StockDO> stockDOList = stockMapper.selectBatchIds(idList);
        return StockConvert.INSTANCE.convertStockDTOList(stockDOList);
    }

    @Override
    public List<String> handleManufactureBillAndStock(BillSaveReqVO childBillSaveReqVO, BillSaveReqVO parentBillSaveReqVO) {
        log.info("加工单-库存处理,子产品单据：{}，父产品单据：{}", JSONUtil.toJsonStr(childBillSaveReqVO), JSONUtil.toJsonStr(parentBillSaveReqVO));

        // 0. 校验参数
        if (ObjectUtil.isNull(childBillSaveReqVO) || CollectionUtils.isEmpty(childBillSaveReqVO.getBillItemSaveReqVOList())) {
            throw exception(CKBILL_CHILDREN_MANUFACTURE_BILL_NOT_ENOUGH);
        }
        if (ObjectUtil.isNull(parentBillSaveReqVO) || CollectionUtils.isEmpty(parentBillSaveReqVO.getBillItemSaveReqVOList())) {
            throw exception(YSBILL_PARENT_MANUFACTURE_BILL_NOT_ENOUGH);
        }
        List<String> result = new ArrayList<>();
        // 1. 子产品出库
        childBillSaveReqVO.setId(null).setNoticeCode(null).setBillType(StockTypeEnum.OUT_STOCK.getValue()).setRemark("");
        childBillSaveReqVO.setBillTime(LocalDateTime.now()).setBillStatus(StockBillStatusEnum.CONFIRMED.getValue());
        // 1.1 创建出库单
        boolean childBillSaveFlag = billService.createBill(childBillSaveReqVO) != null;
        if (!childBillSaveFlag) {
            throw exception(CKBILL_CHILDREN_MANUFACTURE_BILL_FAIL);
        }
        List<BillItemSaveReqVO> billItemSaveReqVOList = childBillSaveReqVO.getBillItemSaveReqVOList();
        // 1.2 明细出库及扣减库存
        boolean childBillItemFlag = billService.subStockAndItemList(BeanUtils.toBean(billItemSaveReqVOList, BillItemDO.class), Boolean.TRUE, StockTypeEnum.OUT_STOCK.getValue());
        if (!childBillItemFlag) {
            throw exception(CKBILL_CHILDREN_MANUFACTURE_BILL_ITEM_FAIL);
        }

        // 2. 主产品入库
        // 2.1 创建单据&明细
        parentBillSaveReqVO.setId(null).setCode(null).setBillType(StockTypeEnum.IN_STOCK.getValue()).setRemark("");
        parentBillSaveReqVO.setBillTime(LocalDateTime.now()).setBillStatus(StockBillStatusEnum.CONFIRMED.getValue());
        parentBillSaveReqVO.getBillItemSaveReqVOList().stream().forEach(x -> {
            String batchCode = batchCodeUtil.genBatchCode(CommonDict.EMPTY_STR);
            result.add(batchCode);
            // 入库操作设置批次号
            x.setBatchCode(batchCode);
        });
        Long billId = billService.createBill(parentBillSaveReqVO).getId();
        boolean parentBillSaveFlag = billId > 0;
        if (!parentBillSaveFlag) {
            throw exception(YSBILL_PARENT_MANUFACTURE_BILL_FAIL);
        }

        // 2.2 处理库存批次信息
        BillRespVO billRespVO = billService.getBill(billId);
        List<BillItemDO> billItemDOList = BeanUtils.toBean(billRespVO.getChildren(), BillItemDO.class);

        Map<Long, BillItemDO> billItemDOMap = billItemDOList.stream().collect(Collectors.toMap(BillItemDO::getSkuId, Function.identity()));
        List<StockDO> parentStockDOList = BeanUtils.toBean(billItemDOList, StockDO.class);
        // 2.3 入库操作更新库存
        Integer defaultQuantity = BigDecimal.ZERO.intValue();
        List<String> skuCodeList = parentStockDOList.stream().map(StockDO::getSkuCode).distinct().toList();
        Map<String, String> basicSkuCodeMap = skuApi.getBasicSkuCode(skuCodeList);
        parentStockDOList.forEach(stockDO -> {
            BillItemDO billItemDO = billItemDOMap.get(stockDO.getSkuId());
            stockDO.setId(null).setCreator(null).setCreateTime(null).setUpdater(null).setUpdateTime(null);
            stockDO.setBillId(billId).setBillItemId(billItemDO.getId()).setReceiptTime(LocalDateTime.now());
            stockDO.setPurchaseContractId(billItemDO.getPurchaseContractId()).setPurchaseContractCode(billItemDO.getPurchaseContractCode());
            Integer actQuantity = billItemDO.getActQuantity();
            stockDO.setInitQuantity(actQuantity).setAvailableQuantity(actQuantity)
                    .setUsedQuantity(defaultQuantity).setProducingQuantity(defaultQuantity).setLockQuantity(defaultQuantity);
            stockDO.setSaleContractId(billRespVO.getSaleContractId()).setSaleContractCode(billRespVO.getSaleContractCode());
            if (CollUtil.isNotEmpty(basicSkuCodeMap)) {
                String basicSkuCode = basicSkuCodeMap.get(stockDO.getSkuCode());
                stockDO.setBasicSkuCode(StrUtil.isEmpty(basicSkuCode) ? CommonDict.EMPTY_STR : basicSkuCode);
            }
        });
        boolean parentStockFlag = stockService.saveBatch(stockService.updateTotalAmount(parentStockDOList));
        if (!parentStockFlag) {
            throw exception(YSBILL_PARENT_MANUFACTURE_BILL_ITEM_FAIL);
        }
        // parentStockDOList.forEach(x -> skuStockCollect.put(x.getSkuCode(), BeanUtils.toBean(x, StockDTO.class)));
        return result;
    }

    @Override
    public Map<String, Map<String, Integer>> validateStockByContractCodeList(List<String> saleContractCodeList) {
        return stockLockService.validateStockByContractCodeList(saleContractCodeList);
    }

    @Override
    public Map<Long, PriceQuantityEntity> getStockPriceMap(List<Long> ids) {
        return stockLockService.getStockPriceMap(ids);
    }

    @Override
    public void deleteStockByPurchaseContractCode(List<String> purchaseContractCode) {
        stockService.deleteStockByPurchaseContractCode(purchaseContractCode);
    }

    @Override
    public Map<Long, Integer> getLockCountBySaleContractCode(String code) {
        return stockLockService.getLockCountBySaleContractCode(code);

    }

    @Override
    public Map<Long, List<StockLockRespVO>> getStockLockMapBySaleCode(String code) {
        List<StockLockDO> list = stockLockService.getStockLockListBySaleCode(code);
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        List<StockLockRespVO> vos = BeanUtils.toBean(list, StockLockRespVO.class);
        return vos.stream().collect(Collectors.groupingBy(StockLockRespVO::getSaleContractItemId));
    }

    @Override
    public List<StockLockRespVO> getStockLockListBySaleCode(String code) {
        List<StockLockDO> list = stockLockService.getStockLockListBySaleCode(code);
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        return BeanUtils.toBean(list, StockLockRespVO.class);
    }

    @Override
    public void deleteStockByPurchaseContractCode(String code) {
        stockService.deleteByPurchaseContractCode(code);
    }

    @Override
    public void rollBackLockQuantity(Long id) {
        stockService.rollBackLockQuantity(id);
    }

    @Override
    public Map<String, Integer> getNotOutStockByPurchaseContractCode(String contractCode, List<String> skuCodeList) {
        return stockService.getNotOutStockByPurchaseContractCode(contractCode, skuCodeList);
    }

    @Override
    public List<StockDTO> getStockDTOByBatchCodes(Set<String> batchCodeList) {
        List<StockDO> stockDOList = stockMapper.selectList(StockDO::getBatchCode, batchCodeList);
        if (CollUtil.isEmpty(stockDOList)) {
            return List.of();
        }
        return StockConvert.INSTANCE.convertStockDTOList(stockDOList);
    }

    @Override
    public boolean updateStockByPurchaseContractCode(String purchaseContractCode, Map<String, BaseSkuEntity> skuQuantityMap) {
        if (StrUtil.isEmpty(purchaseContractCode) || CollUtil.isEmpty(skuQuantityMap)) {
            return false;
        }
        List<BillItemDO> billItemDOList = billService.getBillItemByPurchaseContractCode(purchaseContractCode, skuQuantityMap.keySet());
        // 更新入库单明细
        if (CollUtil.isNotEmpty(billItemDOList)) {
            billItemDOList.forEach(s -> {
                String skuCode = s.getSkuCode();
                BaseSkuEntity baseSkuEntity = skuQuantityMap.get(skuCode);
                if (Objects.isNull(baseSkuEntity)){
                    return;
                }
                Integer quantity = baseSkuEntity.getQuantity();
                if (Objects.nonNull(quantity)) {
                    s.setActQuantity(s.getActQuantity() + quantity);
                    s.setOrderQuantity(s.getOrderQuantity() + quantity);
                }
            });
            billService.updateBillItemList(billItemDOList);
        }
        // 更新库存明细
        List<StockDO> stockDOList = stockService.getStockDOByPurchaseContractCode(purchaseContractCode, skuQuantityMap.keySet());
        AtomicReference<Boolean> result = new AtomicReference<>(false);
        if (CollUtil.isNotEmpty(stockDOList)) {
            stockDOList.forEach(s -> {
                String skuCode = s.getSkuCode();
                BaseSkuEntity baseSkuEntity = skuQuantityMap.get(skuCode);
                if (Objects.isNull(baseSkuEntity)){
                    return;
                }
                Integer quantity = baseSkuEntity.getQuantity();
                int producingQuantity = Objects.isNull(s.getProducingQuantity()) ? 0 : s.getProducingQuantity();
                if (Objects.isNull(quantity)) {
                    return;
                }
                // 如果数量减少优先扣减在制数量
                if (quantity>0){
                    s.setProducingQuantity(producingQuantity + quantity);
                    result.set(true);
                }else {
                    if (producingQuantity - Math.abs(quantity) <0){
                        throw exception(PRODUCING_QUANTITY_NOT_ENOUGH,producingQuantity,Math.abs(quantity));
                    }else {
                        s.setProducingQuantity(producingQuantity - Math.abs(quantity));
                    }
                }
                s.setInitQuantity(s.getInitQuantity() + quantity);
                s.setSpecificationList(baseSkuEntity.getSpecificationList());
                s.setQtyPerOuterbox(baseSkuEntity.getQtyPerOuterbox());
                s.setPrice(baseSkuEntity.getWithTaxPrice());
                BigDecimal boxCount = NumUtil.div(s.getInitQuantity(), s.getQtyPerOuterbox()).setScale(0, RoundingMode.UP);
                JsonWeight grossweight = CalcSpecificationUtil.calcTotalGrossweightByBoxCount(s.getSpecificationList(), boxCount);
                s.setTotalWeight(grossweight);
            });
            stockService.updateBatchById(stockDOList);
        }
        return result.get();
    }

    @Override
    public void validateStock(String purchaseContractCode, Map<String, BaseSkuEntity> skuQuantityMap) {
        if (StrUtil.isEmpty(purchaseContractCode) || CollUtil.isEmpty(skuQuantityMap)) {
            return;
        }
        // 更新库存明细
        List<StockDO> stockDOList = stockService.getStockDOByPurchaseContractCode(purchaseContractCode, skuQuantityMap.keySet());
        if (CollUtil.isNotEmpty(stockDOList)) {
            stockDOList.forEach(s -> {
                String skuCode = s.getSkuCode();
                BaseSkuEntity baseSkuEntity = skuQuantityMap.get(skuCode);
                int producingQuantity = Objects.isNull(s.getProducingQuantity()) ? 0 : s.getProducingQuantity();
                if (Objects.isNull(baseSkuEntity)) {
                    return;
                }
                Integer quantity = Objects.isNull(baseSkuEntity.getQuantity())? 0 : baseSkuEntity.getQuantity();
                // 如果数量减少优先扣减在制数量
                if (quantity>0){
                    s.setProducingQuantity(producingQuantity + quantity);
                }else {
                    if (producingQuantity - Math.abs(quantity) <0){
                        throw exception(PRODUCING_QUANTITY_NOT_ENOUGH,producingQuantity,Math.abs(quantity));
                    }else {
                        s.setProducingQuantity(producingQuantity - Math.abs(quantity));
                    }
                }
                s.setInitQuantity(s.getInitQuantity() + quantity);
                s.setSpecificationList(baseSkuEntity.getSpecificationList());
                s.setQtyPerOuterbox(baseSkuEntity.getQtyPerOuterbox());
                s.setPrice(baseSkuEntity.getWithTaxPrice());
                BigDecimal boxCount = NumUtil.div(s.getInitQuantity(), s.getQtyPerOuterbox()).setScale(0, RoundingMode.UP);
                JsonWeight grossweight = CalcSpecificationUtil.calcTotalGrossweightByBoxCount(s.getSpecificationList(), boxCount);
                s.setTotalWeight(grossweight);
            });
        }
    }

    @Override
    public Map<String, Map<String, JsonAmount>> calcStockCost(List<String> saleContractCodes) {
        if (CollUtil.isEmpty(saleContractCodes)) {
            return Map.of();
        }
        Map<String, Map<String, Integer>> stockLockQuantityMap = stockLockService.getStockLockQuantityBySaleContractCodes(saleContractCodes);
        if (CollUtil.isEmpty(stockLockQuantityMap)) {
            return Map.of();
        }
        List<String> batchCodes = stockLockQuantityMap.values().stream()
                .flatMap(map -> map.keySet().stream())
                .toList();
        Map<String, StockDO> stockMap = stockService.getStockListByBatchCode(batchCodes);
        if (CollUtil.isEmpty(stockMap)) {
            return Map.of();
        }
        Map<String, Map<String, JsonAmount>> result = new HashMap<>();
        stockLockQuantityMap.forEach((saleContractCode, batchCodeMap) -> {
            if (CollUtil.isEmpty(batchCodeMap)) {
                return;
            }
            // 根据产品汇总库存 方便后续计算
            Map<String, List<StockDO>> skuStockMap = new HashMap<>();
            batchCodeMap.forEach((batchCode, quantity) -> {
                StockDO stockDO = stockMap.get(batchCode);
                if (Objects.isNull(stockDO)) {
                    return;
                }
                StockDO calcStockDO = BeanUtil.copyProperties(stockDO, StockDO.class);
                calcStockDO.setLockQuantity(quantity);
                skuStockMap.computeIfAbsent(stockDO.getSkuCode(), k -> new ArrayList<>()).add(calcStockDO);
            });
            if (CollUtil.isNotEmpty(skuStockMap)) {
                // 不同批次产品库存价格取算术平均值
                skuStockMap.forEach((skuCode, stockDOList) -> {
                    if (CollUtil.isEmpty(stockDOList)) {
                        return;
                    }
                    JsonAmount totalAmount = stockDOList.stream()
                            .map(stock -> {
                                BigDecimal price = stock.getPrice().getAmount();
                                Integer lockQuantity = stock.getLockQuantity();
                                String currency = stock.getPrice().getCurrency();
                                BigDecimal amount = price.multiply(BigDecimal.valueOf(lockQuantity));
                                return new JsonAmount(amount, currency);
                            })
                            .reduce(new JsonAmount(BigDecimal.ZERO, "CNY"), JsonAmount::add);
                    result.computeIfAbsent(saleContractCode, s -> new HashMap<>()).put(skuCode, totalAmount);
                });
            }
        });
        return result;
    }

    @Override
    public Map<String, JsonAmount> getStockPrice(List<String> batchCodeList) {
        if (CollUtil.isEmpty(batchCodeList)) {
            return Map.of();
        }
        List<StockDO> stockDOS = stockMapper.selectList(StockDO::getBatchCode, batchCodeList);
        if (CollUtil.isEmpty(stockDOS)) {
            return Map.of();
        }
        return stockDOS.stream().collect(Collectors.toMap(StockDO::getBatchCode, StockDO::getPrice));
    }

    @Override
    public void updateStockCabinetQuantity(Map<String, Map<String, Integer>> updateQunatityMap) {
        if (CollUtil.isEmpty(updateQunatityMap)) {
            return;
        }
        Set<String> batchCodeSet = updateQunatityMap.values().stream().flatMap(map -> map.keySet().stream()).collect(Collectors.toSet());
        List<StockDO> stockDOList = stockMapper.selectList(StockDO::getBatchCode, batchCodeSet);
        List<StockLockDO> stockLockDOS = stockLockMapper.selectList(StockLockDO::getBatchCode, batchCodeSet);
        if (CollUtil.isNotEmpty(stockDOList)) {
            stockDOList.forEach(s -> {
                Map<String, Integer> stockQuantityMap = updateQunatityMap.get(s.getSaleContractCode());
                if (CollUtil.isEmpty(stockQuantityMap)) {
                    return;
                }
                Integer updateQuantity = stockQuantityMap.get(s.getBatchCode());
                if (Objects.isNull(updateQuantity)) {
                    return;
                }
                s.setCabinetQuantity(s.getCabinetQuantity() + updateQuantity);
            });
            stockMapper.updateBatch(stockDOList);
        }
        if (CollUtil.isNotEmpty(stockLockDOS)) {
            stockLockDOS.forEach(s -> {
                Map<String, Integer> stockQuantityMap = updateQunatityMap.get(s.getSaleContractCode());
                if (CollUtil.isEmpty(stockQuantityMap)) {
                    return;
                }
                Integer updateQuantity = stockQuantityMap.get(s.getBatchCode());
                if (Objects.isNull(updateQuantity)) {
                    return;
                }
                s.setCabinetQuantity(s.getCabinetQuantity() + updateQuantity);
            });
            stockLockMapper.updateBatch(stockLockDOS);
        }
    }

    @Override
    public Map<String, Integer> getNoticedQuantityMap(String purchaseContractCode) {
        if (StrUtil.isEmpty(purchaseContractCode)) {
            return Map.of();
        }
        return stockNoticeService.getStockNoticeMapByPurchaseContractCode(purchaseContractCode);
    }

    @Override
    public Map<String, List<ShipmentPriceEntity>> getStockPriceBySaleContractCode(List<String> saleContractCodeList) {
        Map<String, List<ShipmentPriceEntity>> result = new HashMap<>();
        if (CollUtil.isEmpty(saleContractCodeList)){
            return result;
        }
        return billService.getStockPriceBySaleContractCode(saleContractCodeList);
    }

    @Override
    public Map<String, LocalDateTime> getOutStockTimeBySaleItems(List<String> saleItemUniqueCodeList) {
       if (CollUtil.isEmpty(saleItemUniqueCodeList)){
           return Map.of();
       }
       return billService.getOutStockTimeBySaleItems(saleItemUniqueCodeList);
    }

    @Override
    public Map<Long, List<StockDTO>> getStockDTOMapBySaleContractCodes(List<String> saleContractCodeList) {
        Map<Long, List<StockDTO>> result = new HashMap<>();
        if (CollUtil.isEmpty(saleContractCodeList)) {
            return result;
        }
        
        // 1. 先查询库存锁定信息
        List<StockLockRespVO> stockLockRespVOList = getStockLockRespVOBySaleContractCodes(saleContractCodeList);
        if (CollUtil.isEmpty(stockLockRespVOList)) {
            return result;
        }
        
        // 2. 获取批次号集合
        Set<String> batchCodeSets = stockLockRespVOList.stream()
                .map(StockLockRespVO::getBatchCode)
                .distinct()
                .collect(Collectors.toSet());
        
        // 3. 根据批次号查询库存明细
        List<StockDTO> stockDTOList = getStockDTOByBatchCodes(batchCodeSets);
        if (CollUtil.isEmpty(stockDTOList)) {
            return result;
        }
        
        // 4. 创建批次号到库存明细的映射
        Map<String, List<StockDTO>> batchCodeToStockMap = stockDTOList.stream()
                .collect(Collectors.groupingBy(StockDTO::getBatchCode));
        
        // 5. 按销售合同明细ID分组，并关联库存信息
        Map<Long, List<StockDTO>> saleContractItemIdToStockMap = new HashMap<>();
        for (StockLockRespVO stockLock : stockLockRespVOList) {
            Long saleContractItemId = stockLock.getSaleContractItemId();
            String batchCode = stockLock.getBatchCode();
            
            List<StockDTO> stockList = batchCodeToStockMap.get(batchCode);
            if (CollUtil.isNotEmpty(stockList)) {
                saleContractItemIdToStockMap.computeIfAbsent(saleContractItemId, k -> new ArrayList<>())
                        .addAll(stockList);
            }
        }
        
        return saleContractItemIdToStockMap;
    }



    @Override
    public List<StockDTO> getStockDTOByPurchaseContractCodes(List<String> purchaseContractCodes) {
        if (CollUtil.isEmpty(purchaseContractCodes)) {
            return new ArrayList<>();
        }
        
        // 根据采购合同号列表查询库存
        List<StockDO> stockDOList = stockMapper.selectList(
            new LambdaQueryWrapper<StockDO>()
                .in(StockDO::getPurchaseContractCode, purchaseContractCodes)
        );
        
        if (CollUtil.isEmpty(stockDOList)) {
            return new ArrayList<>();
        }
        
        // 转换为DTO
        return BeanUtils.toBean(stockDOList, StockDTO.class);
    }
}
