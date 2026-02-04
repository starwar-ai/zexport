package com.syj.eplus.module.wms.service.bill;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.dict.CommonDict;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.api.dept.DeptApi;
import cn.iocoder.yudao.module.system.api.dept.dto.DeptRespDTO;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syj.eplus.framework.common.dict.CalculationDict;
import com.syj.eplus.framework.common.entity.*;
import com.syj.eplus.framework.common.enums.BillStatusEnum;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.framework.common.enums.ContractItemBillStatusEnum;
import com.syj.eplus.framework.common.enums.ShippedAddressEnum;
import com.syj.eplus.module.dms.enums.api.ShipmentApi;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import com.syj.eplus.module.pms.api.sku.SkuApi;
import com.syj.eplus.module.pms.api.sku.dto.SkuDTO;
import com.syj.eplus.module.scm.api.purchasecontract.PurchaseContractApi;
import com.syj.eplus.module.sms.api.SaleContractApi;
import com.syj.eplus.module.wms.api.stock.dto.BillItemSaveReqVO;
import com.syj.eplus.module.wms.api.stock.dto.BillSaveReqVO;
import com.syj.eplus.module.wms.api.warehouse.IWarehouseApi;
import com.syj.eplus.module.wms.api.warehouse.dto.WarehouseDTO;
import com.syj.eplus.module.wms.controller.admin.bill.vo.BillPageReqVO;
import com.syj.eplus.module.wms.controller.admin.bill.vo.BillRespVO;
import com.syj.eplus.module.wms.controller.admin.billitem.vo.BillItemRespVO;
import com.syj.eplus.module.wms.convert.bill.BillConvert;
import com.syj.eplus.module.wms.convert.bill.BillItemConvert;
import com.syj.eplus.module.wms.dal.dataobject.bill.BillDO;
import com.syj.eplus.module.wms.dal.dataobject.bill.BillItemDO;
import com.syj.eplus.module.wms.dal.dataobject.stock.StockDO;
import com.syj.eplus.module.wms.dal.dataobject.stockNotice.StockNoticeDO;
import com.syj.eplus.module.wms.dal.dataobject.stockNotice.StockNoticeItemDO;
import com.syj.eplus.module.wms.dal.dataobject.stocklock.StockLockDO;
import com.syj.eplus.module.wms.dal.mysql.bill.BillItemMapper;
import com.syj.eplus.module.wms.dal.mysql.bill.BillMapper;
import com.syj.eplus.module.wms.dal.mysql.stockNotice.StockNoticeItemMapper;
import com.syj.eplus.module.wms.dal.mysql.stockNotice.StockNoticeMapper;
import com.syj.eplus.module.wms.enums.NoticeStatusEnum;
import com.syj.eplus.module.wms.enums.StockBillStatusEnum;
import com.syj.eplus.module.wms.enums.StockSourceTypeEnum;
import com.syj.eplus.module.wms.enums.StockTypeEnum;
import com.syj.eplus.module.wms.service.stock.StockService;
import com.syj.eplus.module.wms.service.stockNotice.StockNoticeService;
import com.syj.eplus.module.wms.service.stocklock.StockLockService;
import com.syj.eplus.module.wms.util.BatchCodeUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.wms.enums.ErrorCodeConstants.*;

/**
 * 仓储管理-入(出)库单 Service 实现类
 *
 * @author ePlus
 */
@Service
@Validated
public class BillServiceImpl extends ServiceImpl<BillMapper, BillDO> implements BillService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private BillMapper billMapper;
    @Resource
    private BillItemMapper billItemMapper;
    @Resource
    private CodeGeneratorApi codeGeneratorApi;
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private DeptApi deptApi;
    @Resource
    private SkuApi skuApi;
    @Resource
    private BatchCodeUtil batchCodeUtil;
    @Resource
    private StockService stockService;
    @Lazy
    @Resource
    private StockNoticeService stockNoticeService;
    @Resource
    private StockLockService stockLockService;

    @Resource
    private PurchaseContractApi purchaseContractApi;

    @Resource
    private IWarehouseApi warehouseApi;

    @Resource
    private StockNoticeItemMapper stockNoticeItemMapper;

    @Resource
    private StockNoticeMapper stockNoticeMapper;
    @Resource
    private SaleContractApi saleContractApi;
    @Resource
    private ShipmentApi shipmentApi;

    private static final String SN_TYPE = "SN_BILL";
    // 入库验收单单号规则
    private static final String YS_CODE_PREFIX = "YS";
    // 出库验收单单号规则
    private static final String CK_CODE_PREFIX = "CK";


    @Override
    @Transactional(rollbackFor = Exception.class)
    public CreatedResponse createBill(BillSaveReqVO createReqVO) {
        BillDO bill = BillConvert.INSTANCE.convertBillDO(createReqVO);
        bill.setSourceType(createReqVO.getSourceType());
        // 0. 补全单据信息
        String CODE_PREFIX = "";
        if (createReqVO.getBillType() == StockTypeEnum.IN_STOCK.getValue()) {
            CODE_PREFIX = YS_CODE_PREFIX;
        } else {
            CODE_PREFIX = CK_CODE_PREFIX;
        }
        if (ObjectUtil.isNull(bill.getBillStatus())) {
            bill.setBillStatus(StockBillStatusEnum.UN_CONFIRM.getValue());
        }
        bill.setId(null);
        bill.setCode(codeGeneratorApi.getCodeGenerator(SN_TYPE, CODE_PREFIX));
        bill.setPrintFlag(BooleanEnum.NO.getValue());
        bill.setPrintTimes(BigDecimal.ZERO.intValue());

        // 出入库单增加产品编码查询
        List<BillItemSaveReqVO> billItemSaveReqVOList = createReqVO.getBillItemSaveReqVOList();
        String skuCodes = billItemSaveReqVOList.stream().map(BillItemSaveReqVO::getSkuCode).collect(Collectors.joining(","));
        bill.setSkuCodes(skuCodes);

        // 1. 保存单据主表
        boolean billFlag = billMapper.insert(bill) > 0;
        // 2. 保存单据子表
        List<BillItemDO> billItemDOList = BeanUtils.toBean(billItemSaveReqVOList, BillItemDO.class);
        boolean billItemFlag = updateBillItemList(bill.getId(), bill.getCode(), bill.getBillType(), billItemDOList, true);
        // 回写出入库通知单明细转单标识
        List<String> sourceUniqueCodeList = billItemDOList.stream().map(BillItemDO::getSourceUniqueCode).distinct().toList();
        stockNoticeItemMapper.update(new StockNoticeItemDO().setConvertBillFlag(BooleanEnum.YES.getValue()),new LambdaQueryWrapper<StockNoticeItemDO>().in(StockNoticeItemDO::getUniqueCode, sourceUniqueCodeList));
        if (ShippedAddressEnum.FACTORY.getValue().equals(createReqVO.getFactoryFlag())){
            // 更新出运明细中的出库数量跟日期
            updateShipmentMsg(billItemSaveReqVOList,bill.getId(),LocalDateTime.now());
            // 出库单明细+库存
            this.subStockAndItemList(billItemDOList, Boolean.FALSE,StockTypeEnum.OUT_STOCK.getValue());
            // 生成内部合同
            String genContractUniqueCode = IdUtil.fastSimpleUUID();
            billMapper.updateById(new BillDO().setId(bill.getId()).setBillStatus(StockBillStatusEnum.CONFIRMED.getValue()).setGenContractUniqueCode(genContractUniqueCode));
            Map<String, Integer> updateMap = billItemDOList.stream().collect(Collectors.groupingBy(BillItemDO::getSaleItemUniqueCode, Collectors.summingInt(BillItemDO::getActQuantity)));
            saleContractApi.genInternalContractByOutBill(updateMap,genContractUniqueCode);
        }
        if (billFlag && billItemFlag) {
            return new CreatedResponse(bill.getId(),bill.getCode()) ;
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBill(BillSaveReqVO updateReqVO) {
        // 0. 校验存在
        Long billId = updateReqVO.getId();
        BillDO billDO = validateBillExists(billId);

        Integer billType = updateReqVO.getBillType();
        Integer billStatus = updateReqVO.getBillStatus();
        LocalDateTime outTime = LocalDateTime.now();
        // 1. 更新单据主表
        BillDO updateBillDO = BillConvert.INSTANCE.convertBillDO(updateReqVO);
        boolean billFlag = billMapper.updateById(updateBillDO) > 0;

        // 2. 更新单据子表
        List<BillItemSaveReqVO> itemSaveReqVOList = updateReqVO.getBillItemSaveReqVOList();
        List<BillItemDO> updateBillItemDOList = BeanUtils.toBean(itemSaveReqVOList, BillItemDO.class);
        if (billStatus == StockBillStatusEnum.CONFIRMED.getValue()) {
            if (billType == StockTypeEnum.IN_STOCK.getValue()) {
                // 入库提交操作设置明细批次号
                updateBillItemDOList.forEach(x -> {
                    x.setSourceType(StockSourceTypeEnum.PURCHASE.getValue());
                });
            }
            // 更新出入库通知单已转出入库单数量
            updateNoticeQuantity(updateBillItemDOList, true, false);
        }
        boolean billItemFlag = updateBillItemList(billId, updateBillDO.getCode(), billType, updateBillItemDOList, true);

        // 3. 处理库存及明细
        if (billStatus == StockBillStatusEnum.CONFIRMED.getValue()) {
            // 更新确认状态
            updateBillDO.setBillTime(outTime);
            updateBillDO.setBillStatus(StockBillStatusEnum.CONFIRMED.getValue());
            billMapper.updateById(updateBillDO);
            if (billType.intValue() == StockTypeEnum.IN_STOCK.getValue()) {
                // 出供应商可用库存、入泛太库存、保持锁定库存
                this.addStockAndItemList(billId, updateBillItemDOList);
                // 更新外销合同明细中入库状态
                Long saleContractId = updateBillDO.getSaleContractId();
                List<BillItemSaveReqVO> billItemSaveReqVOList = updateReqVO.getBillItemSaveReqVOList();
                List<Long> billItemIdList = billItemSaveReqVOList.stream().map(BillItemSaveReqVO::getId).distinct().toList();
                List<BillItemDO> billItemDOS = billItemMapper.selectBatchIds(billItemIdList);
                Map<Long, Integer> baseBillQuantity = billItemDOS.stream().collect(Collectors.toMap(BillItemDO::getId, BillItemDO::getActQuantity));
                if (!CollectionUtils.isEmpty(billItemSaveReqVOList)) {
                    List<String> saleItemUniqueCode = billItemSaveReqVOList.stream().map(BillItemSaveReqVO::getSaleItemUniqueCode).filter(StrUtil::isNotEmpty).toList();
                    if (CollUtil.isNotEmpty(saleItemUniqueCode)){
                        saleContractApi.updateSaleItemBillStatus(saleContractId, saleItemUniqueCode, ContractItemBillStatusEnum.VERTAK_STORE.getStatus());
                    }
                }
                // 更新采购合同明细中的入库状态
                if (!CollectionUtils.isEmpty(billItemSaveReqVOList)) {
                    List<String> purchaseItemUniqueCodeList = billItemSaveReqVOList.stream().map(BillItemSaveReqVO::getPurchaseItemUniqueCode).filter(StrUtil::isNotEmpty).toList();
                    if (CollUtil.isNotEmpty(purchaseItemUniqueCodeList)){
                        Map<String, Integer> wmsBillMap = billItemSaveReqVOList.stream().filter(s -> StrUtil.isNotEmpty(s.getPurchaseItemUniqueCode())).collect(Collectors.toMap(BillItemSaveReqVO::getPurchaseItemUniqueCode, BillItemSaveReqVO::getActQuantity));
                        Map<String, Integer> diffBillQuantity = new HashMap<>();
                        billItemSaveReqVOList.forEach(s->{
                            if (CollUtil.isNotEmpty(baseBillQuantity)){
                                Integer baseQuantity = baseBillQuantity.get(s.getId());
                                diffBillQuantity.put(s.getPurchaseItemUniqueCode(),s.getActQuantity() -baseQuantity);
                            }
                        });
                        purchaseContractApi.updatePurchaseItem(saleContractId, null, purchaseItemUniqueCodeList, ContractItemBillStatusEnum.VERTAK_STORE.getStatus(), wmsBillMap,diffBillQuantity);
                    }
                }

            } else {
                List<BillItemSaveReqVO> billItemSaveReqVOList = updateReqVO.getBillItemSaveReqVOList();
                // 更新出运明细中的出库数量跟日期
                updateShipmentMsg(billItemSaveReqVOList,billId,outTime);
                // 出库单明细+库存
                this.subStockAndItemList(updateBillItemDOList, Boolean.FALSE,StockTypeEnum.OUT_STOCK.getValue());
                // 生成内部合同
                String genContractUniqueCode = IdUtil.fastSimpleUUID();
                billMapper.updateById(new BillDO().setId(billId).setGenContractUniqueCode(genContractUniqueCode));
                Map<String, Integer> updateMap = updateBillItemDOList.stream().filter(x -> StrUtil.isNotEmpty(x.getSaleItemUniqueCode())).collect(Collectors.groupingBy(BillItemDO::getSaleItemUniqueCode, Collectors.summingInt(BillItemDO::getActQuantity)));
                if (CollUtil.isNotEmpty(updateMap)){
                    saleContractApi.genInternalContractByOutBill(updateMap,genContractUniqueCode);
                }
            }
            // 回写出入库通知单明细转单标识
            List<String> sourceUniqueCodeList = updateBillItemDOList.stream().map(BillItemDO::getSourceUniqueCode).distinct().toList();
            stockNoticeItemMapper.update(new StockNoticeItemDO().setConvertBillFlag(BooleanEnum.YES.getValue()),new LambdaQueryWrapper<StockNoticeItemDO>().in(StockNoticeItemDO::getUniqueCode, sourceUniqueCodeList));
            // 回写出入库通知单转单标识
            stockNoticeService.updateConvertStatusByCode(billDO.getNoticeCode());
        } else {
            updateNoticeQuantity(updateBillItemDOList, false, false);
        }
        return billFlag && billItemFlag;
    }
    private void updateShipmentMsg(List<BillItemSaveReqVO> billItemSaveReqVOList,Long billId,LocalDateTime outTime){
        if (CollUtil.isEmpty(billItemSaveReqVOList)){
            return;
        }
            Map<Long, Integer> skuStockMap = billItemSaveReqVOList.stream()
                    .filter(x -> Objects.nonNull(x.getShipmentItemId()))
                    .collect(Collectors.groupingBy(
                                    BillItemSaveReqVO::getShipmentItemId,
                                    Collectors.summingInt(BillItemSaveReqVO::getActQuantity)
                            )
                    );
            BillDO billDO = billMapper.selectById(billId);
            StockNoticeDO stockNoticeDO = stockNoticeMapper.selectOne(new LambdaQueryWrapper<StockNoticeDO>().eq(StockNoticeDO::getCode, billDO.getNoticeCode()));
            // 回写出运明细
            if (Objects.nonNull(stockNoticeDO) && StrUtil.isNotEmpty(stockNoticeDO.getShipmentCode())) {
                shipmentApi.updateOutQuantity(stockNoticeDO.getShipmentCode(), skuStockMap, outTime);
            }
    }
    /**
     * 更新出入库通知单已转单数量
     *
     * @param billItemDOList 出入库单明细列表
     * @param submitFlag     是否提交 true -影响实际转单数量  false - 仅影响转单中数量
     * @param isCancel       是否作废  作废时仅回滚实际转单数量
     */
    private void updateNoticeQuantity(List<BillItemDO> billItemDOList, Boolean submitFlag, Boolean isCancel) {
        Map<String, Integer> quantityMap = billItemDOList.stream().collect(Collectors.toMap(BillItemDO::getSourceUniqueCode, BillItemDO::getActQuantity, (o, n) -> o));
        List<StockNoticeItemDO> noticeItemDOList = stockNoticeItemMapper.selectList(new LambdaQueryWrapper<StockNoticeItemDO>().in(StockNoticeItemDO::getUniqueCode, quantityMap.keySet()));
        if (CollUtil.isNotEmpty(noticeItemDOList)) {
            AtomicReference<Boolean> noticeStatus = new AtomicReference<>(true);
            AtomicReference<Long> noticeId = new AtomicReference<>();
            noticeItemDOList.forEach(s -> {
                String uniqueCode = s.getUniqueCode();
                noticeId.set(s.getNoticeId());
                // 实际转出入库单数量
                Integer realBillQuantity = Objects.isNull(s.getRealBillQuantity()) ? CalculationDict.ZERO : s.getRealBillQuantity();
                // 正在转出入库单数量
                Integer inBillQuantity = Objects.isNull(s.getInBillQuantity()) ? CalculationDict.ZERO : s.getInBillQuantity();
                // 本次转单数量
                Integer thisQuantity = Objects.isNull(quantityMap.get(uniqueCode)) ? CalculationDict.ZERO : quantityMap.get(uniqueCode);
                // 作废回滚数量
                if (isCancel) {
                    // 实际转单数量 = 原实际转单数量 - 本次实际转单数量
                    s.setRealBillQuantity(NumberUtil.sub(realBillQuantity, thisQuantity).intValue());
                    return;
                }
                if (submitFlag) {
                    // 实际转单数量 = 原实际转单数量 + 本次实际转单数量
                    s.setRealBillQuantity(NumberUtil.add(realBillQuantity, thisQuantity).intValue());
                    // 转单中数量 = 原转单中数量 - 本次实际转单数量
                    if (inBillQuantity > 0) {
                        s.setInBillQuantity(NumberUtil.sub(inBillQuantity, thisQuantity).intValue());
                    }
                } else {
                    s.setInBillQuantity(NumberUtil.add(inBillQuantity, thisQuantity).intValue());
                }
            });
            if (CollUtil.isNotEmpty(noticeItemDOList)) {
                noticeItemDOList.forEach(s -> {
                    // 重写待入库数量  待入库数量 = 通知单数量 - 实际转单数量 - 转单中数量
                    Integer orderQuantity = Objects.isNull(s.getOrderQuantity()) ? CalculationDict.ZERO : s.getOrderQuantity();
                    Integer inBillQuantity = Objects.isNull(s.getInBillQuantity()) ? CalculationDict.ZERO : s.getInBillQuantity();
                    Integer realBillQuantity = Objects.isNull(s.getRealBillQuantity()) ? CalculationDict.ZERO : s.getRealBillQuantity();
                    s.setPendingStockQuantity(orderQuantity - inBillQuantity - realBillQuantity);
                    if (s.getPendingStockQuantity() != 0) {
                        noticeStatus.set(false);
                    }
                });
                if (noticeStatus.get()) {
                    stockNoticeMapper.updateById(new StockNoticeDO().setId(noticeId.get()).setNoticeStatus(NoticeStatusEnum.CONVERTED.getValue()));
                } else {
                    stockNoticeMapper.updateById(new StockNoticeDO().setId(noticeId.get()).setNoticeStatus(NoticeStatusEnum.PART_CONVERT.getValue()));
                }
                stockNoticeItemMapper.updateBatch(noticeItemDOList);
            }

        }
    }

    /**
     * 构造出库单-明细列表
     *
     * @param updateBillItemDOList
     * @param saleContractCode
     * @return
     */
    private List<BillItemDO> buildOutBillItemList(List<BillItemDO> updateBillItemDOList, String saleContractCode, List<String> batchCodeList) {
        // 根据销售合同编码与产品编码查询批次-标准采购产品
        List<String> skuCodeList = updateBillItemDOList.stream().map(BillItemDO::getSkuCode).collect(Collectors.toList());
        List<Long> billIdList = updateBillItemDOList.stream().map(BillItemDO::getSourceId).toList();
        LambdaQueryWrapper<StockDO> stockQuery = new LambdaQueryWrapper<>();
        stockQuery.in(StockDO::getBillId, billIdList);
        if (!CollectionUtils.isEmpty(batchCodeList)) {
            stockQuery.in(StockDO::getBatchCode, batchCodeList);
        }
        if (StringUtils.isNotBlank(saleContractCode)) {
            stockQuery.eq(StockDO::getSaleContractCode, saleContractCode);
        }
        stockQuery.in(StockDO::getSkuCode, skuCodeList);
        List<StockDO> standStockDOList = stockService.list(stockQuery);

        // 标准采购产品编码集合
        List<String> standSkuCodeList;
        // 标准采购单据明细集合
        List<BillItemDO> standBillItemList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(standStockDOList)) {
            standSkuCodeList = standStockDOList.stream().map(StockDO::getSkuCode).toList();
            Map<String, String> collect = standStockDOList.stream().collect(Collectors.toMap(StockDO::getSkuCode, StockDO::getBatchCode));
            standBillItemList = updateBillItemDOList.stream().filter(x -> standSkuCodeList.contains(x.getSkuCode())).toList();
            standBillItemList.forEach(x -> x.setBatchCode(collect.get(x.getSkuCode())));
        } else {
            standSkuCodeList = new ArrayList<>();
        }
        // 过滤自营采购产品列表，查询锁定库存表中批次信息
        List<String> ownSkuCodeList = CollUtil.subtractToList(skuCodeList, standSkuCodeList);
        List<BillItemDO> ownBillItemList = updateBillItemDOList.stream().filter(x -> ownSkuCodeList.contains(x.getSkuCode())).toList();
        List<BillItemDO> unAddBillItemList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(ownBillItemList) && StringUtils.isNotBlank(saleContractCode)) {
            LambdaQueryWrapper<StockLockDO> stockLockQuery = new LambdaQueryWrapper<StockLockDO>().eq(StockLockDO::getSaleContractCode, saleContractCode).in(StockLockDO::getSkuCode, ownSkuCodeList);
            List<StockLockDO> stockLockDOList = stockLockService.list(stockLockQuery);
            if (CollectionUtils.isEmpty(stockLockDOList)) {
                throw exception(STOCK_LOCK_NOT_EXISTS);
            }
            Map<String, List<StockLockDO>> collect = stockLockDOList.stream().collect(Collectors.groupingBy(StockLockDO::getSkuCode));
            ownBillItemList.forEach(x -> {
                List<StockLockDO> stockLockDOS = collect.get(x.getSkuCode());
                if (!CollectionUtils.isEmpty(stockLockDOS) && stockLockDOS.size() > 1) {
                    // 移除首个批次信息
                    stockLockDOS.remove(0);
                    // 构造新的单据明细对象
                    for (int i = 0; i < stockLockDOS.size(); i++) {
                        StockLockDO stockLockDO = stockLockDOS.get(i);
                        if (i == 0) {
                            x.setBatchCode(stockLockDO.getBatchCode());
                            x.setOrderQuantity(stockLockDO.getLockQuantity());
                            x.setActQuantity(stockLockDO.getLockQuantity());
                        } else {
                            BillItemDO unAddBillItemDO = BeanUtils.toBean(x, BillItemDO.class);
                            unAddBillItemDO.setBatchCode(stockLockDO.getBatchCode());
                            unAddBillItemDO.setOrderQuantity(stockLockDO.getLockQuantity());
                            unAddBillItemDO.setActQuantity(stockLockDO.getLockQuantity());
                            unAddBillItemList.add(unAddBillItemDO);
                        }
                    }
                } else {
                    // 仅有一个批次时可直接设置
                    x.setBatchCode(stockLockDOS.get(0).getBatchCode());
                }
            });
        }
        updateBillItemDOList = new ArrayList<>();
        updateBillItemDOList.addAll(standBillItemList);
        updateBillItemDOList.addAll(ownBillItemList);
        updateBillItemDOList.addAll(unAddBillItemList);
        return updateBillItemDOList;
    }

    /**
     * 增加库存及入库明细处理
     *
     * @param billId
     * @return
     */
    @Override
    public boolean addStockAndItemList(Long billId, List<BillItemDO> billItemDOList) {
        BillRespVO billRespVO = this.getBill(billId);
        String saleContractCode = billRespVO.getSaleContractCode();
        List<String> purchaseContractCodeList = billRespVO.getChildren().stream().map(BillItemRespVO::getPurchaseContractCode).collect(Collectors.toList());
        List<BillItemRespVO> billItemRespVOList = billRespVO.getChildren();
        List<String> skuCodeList = billItemRespVOList.stream().map(BillItemRespVO::getSkuCode).toList();
        boolean manualFlag = stockNoticeService.validateManualStockNotice(billRespVO.getNoticeCode());
        // 手动创建的入库单只入库
        if (manualFlag&& billRespVO.getBillType()== StockTypeEnum.IN_STOCK.getValue()){
            // 手动创建入库通知单添加库存
                List<StockDO> stockDOList = BeanUtils.toBean(billItemRespVOList, StockDO.class);
                // 入库操作更新库存
                Integer defaultQuantity = BigDecimal.ZERO.intValue();
                Map<Long, BillItemRespVO> billItemRespVOMap = billItemRespVOList.stream().collect(Collectors.toMap(BillItemRespVO::getSkuId, Function.identity()));
                stockDOList.stream().forEach(stockDO -> {
                    BillItemRespVO billItemRespVO = billItemRespVOMap.get(stockDO.getSkuId());
                    stockDO.setId(null).setCreator(null).setCreateTime(null).setUpdater(null).setUpdateTime(null);
                    stockDO.setReceiptTime(LocalDateTime.now());
                    stockDO.setCompanyId(billRespVO.getCompanyId());
                    stockDO.setCompanyName(billRespVO.getCompanyName());
                    stockDO.setWarehouseName(billItemRespVO.getWarehouseName());
                    stockDO.setPosition("默认");
                    stockDO.setBillId(billId);
                    stockDO.setBillItemId(billItemRespVO.getId());
                    stockDO.setBatchCode(billItemRespVO.getBatchCode());
                    stockDO.setPurchaseContractId(billItemRespVO.getPurchaseContractId()).setPurchaseContractCode(billItemRespVO.getPurchaseContractCode());
                    Integer actQuantity = billItemRespVO.getOrderQuantity();
                    stockDO.setInitQuantity(actQuantity).setUsedQuantity(defaultQuantity)
                            .setProducingQuantity(defaultQuantity).setLockQuantity(defaultQuantity);
                    stockDO.setAvailableQuantity(actQuantity);
                });
                stockService.saveBatch(stockService.updateTotalAmount(stockDOList));
                return true;
        }
        // 出库库存
        LambdaQueryWrapper<StockDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(StockDO::getPurchaseContractCode, purchaseContractCodeList);
        queryWrapper.in(StockDO::getSkuCode, skuCodeList);
        List<WarehouseDTO> ownWarehouseList = warehouseApi.listWarehouse(BooleanEnum.NO.getValue(), "");
        queryWrapper.notIn(StockDO::getWarehouseId, ownWarehouseList.stream().map(WarehouseDTO::getId).toList());
        List<StockDO> stockDOList = stockService.list(queryWrapper);
        if (CollectionUtils.isEmpty(stockDOList)) {
            throw exception(STOCK_NOT_EXISTS);
        }
        List<Long> billItemList = stockDOList.stream().map(StockDO::getBillItemId).toList();
        List<BillItemDO> baseBillItemDOList = billItemMapper.selectBatchIds(billItemList);
        Map<String, BillItemDO> billItemDOCollect =new HashMap<>();
        billItemDOList.forEach(s->{
            BillItemDO billItemDO = billItemDOCollect.get(s.getSkuCode());
            if (Objects.isNull(billItemDO)){
                billItemDOCollect.put(s.getSkuCode(),s);
                return;
            }
            billItemDO.setOrderQuantity(billItemDO.getOrderQuantity()+s.getOrderQuantity());
            billItemDO.setActQuantity(billItemDO.getActQuantity()+s.getActQuantity());
            billItemDO.setActBoxQuantity(billItemDO.getActBoxQuantity()+s.getActBoxQuantity());
            billItemDO.setOrderBoxQuantity(billItemDO.getOrderBoxQuantity()+s.getOrderBoxQuantity());
            billItemDOCollect.put(s.getSkuCode(), billItemDO);
        });


        // 1. 创建出库单
        BillSaveReqVO createReqVO = BeanUtils.toBean(billRespVO, BillSaveReqVO.class);
        createReqVO.setId(null).setNoticeCode(null).setBillType(StockTypeEnum.OUT_STOCK.getValue());
        createReqVO.setBillTime(LocalDateTime.now()).setBillStatus(StockBillStatusEnum.CONFIRMED.getValue());
        List<BillItemSaveReqVO> billItemSaveReqVOList = BeanUtils.toBean(billItemRespVOList, BillItemSaveReqVO.class);
        billItemSaveReqVOList.forEach(x -> x.setSourceType(StockSourceTypeEnum.PURCHASE.getValue()));
        createReqVO.setBillItemSaveReqVOList(billItemSaveReqVOList);
        boolean saveBillFlag = this.createBill(createReqVO) != null;

        // 构建锁定库存列表
        List<Long> stockIdList = stockDOList.stream().map(StockDO::getId).toList();
        LambdaQueryWrapper<StockLockDO> stockLockQuery = new LambdaQueryWrapper<>();
        stockLockQuery.in(StockLockDO::getStockId, stockIdList);
        List<StockLockDO> stockLockDOList = stockLockService.list(stockLockQuery);
        List<StockLockDO> baseStockLockDOList = new ArrayList<>();
        BeanUtil.copyProperties(stockLockDOList, baseStockLockDOList);

        // 2. 供应商单据出库扣减库存及明细处理
        List<BillItemDO> updateBillItemDOList = this.buildOutBillItemList(baseBillItemDOList, saleContractCode, null);
        updateBillItemDOList.forEach(s -> {
            s.setActQuantity(billItemDOCollect.get(s.getSkuCode()).getActQuantity());
        });
        boolean originStockFLag = this.subStockAndItemList(updateBillItemDOList, Boolean.TRUE,billRespVO.getBillType());

        // 3. 泛太入库单据库存及锁定库存
        stockDOList.forEach(stockDO -> {
            BillItemDO billItemRespVO = billItemDOCollect.get(stockDO.getSkuCode());
            stockDO.setBatchCode(billItemRespVO.getBatchCode()).setInitQuantity(billItemRespVO.getActQuantity())
                    .setAvailableQuantity(billItemRespVO.getActQuantity()).setUsedQuantity(BigDecimal.ZERO.intValue())
                    .setWarehouseId(billItemRespVO.getWarehouseId()).setWarehouseName(billItemRespVO.getWarehouseName())
                    .setBillId(billItemRespVO.getSourceId()).setBillItemId(billItemRespVO.getId())
                    .setId(null);
        });
        // 保存入库库存
        boolean stockFlag = stockService.saveBatch(stockDOList);
        if (!CollectionUtils.isEmpty(stockLockDOList)) {
            Map<Long, List<StockLockDO>> stockLockCollect = stockLockDOList.stream().collect(Collectors.groupingBy(StockLockDO::getStockId));
            stockDOList.forEach(stockDO -> {
                List<StockLockDO> stockLockDOS = stockLockCollect.get(stockDO.getId());
                if (CollUtil.isNotEmpty(stockLockDOS)){
                    stockLockDOS.forEach(stockLockDO -> stockLockDO.setId(null).setStockId(stockDO.getId()).setBatchCode(stockDO.getBatchCode()));
                }
                stockDO.setId(null);
            });
        }
        // 保存入库库存-锁定表
        if (!CollectionUtils.isEmpty(stockLockDOList)) {
            stockLockDOList.forEach(s->s.setId(null));
            stockLockService.saveBatch(stockLockDOList);
            stockLockService.deleteBatchByIds(baseStockLockDOList.stream().map(StockLockDO::getId).toList());
        }
        return saveBillFlag && originStockFLag && stockFlag;
    }


    /**
     * 扣减库存及明细处理
     *
     * @param billItemDOList
     * @return
     */
    @Override
    public boolean subStockAndItemList(List<BillItemDO> billItemDOList, Boolean convertFlag,Integer stockType) {
        // 根据批次号对出库单明细进行分组
        Map<String, Integer> billItemDOMap = billItemDOList.stream().collect(Collectors.groupingBy(BillItemDO::getBatchCode,Collectors.summingInt(BillItemDO::getActQuantity)));
        Map<String, Map<String, Integer>> stockMap = billItemDOList.stream().filter(s->Objects.nonNull(s.getPurchaseContractCode())&&StockTypeEnum.OUT_STOCK.getValue().equals(s.getBillType())).collect(
                Collectors.groupingBy(
                        BillItemDO::getPurchaseContractCode,
                        Collectors.groupingBy(
                                BillItemDO::getSkuCode,
                                Collectors.summingInt(BillItemDO::getActQuantity)
                        )
                )
        );
        // 根据批次号查询库存信息
        List<String> batchCodeList = billItemDOList.stream().map(BillItemDO::getBatchCode).distinct().collect(Collectors.toList());
        List<StockDO> stockDOList = stockService.list(new LambdaQueryWrapper<StockDO>().in(StockDO::getBatchCode, batchCodeList));

        // 设置可用库存、锁定库存
        stockDOList.forEach(stockDO -> {
            String batchCode = stockDO.getBatchCode();
            // 设置批次及出库库存
            Integer actQuantity = billItemDOMap.get(batchCode);
            stockDO.setReceiptTime(LocalDateTime.now());
            // 已用库存
            int usedQuantity = NumberUtil.add(stockDO.getUsedQuantity(), actQuantity).intValue();
            // 自营采购时需要设置库存，标准采购时无需调整库存
            if (StringUtils.isBlank(stockDO.getSaleContractCode())) {
                // 设置已用库存
                stockDO.setUsedQuantity(usedQuantity);
                // 设置锁定库存
                Integer lockQuantity = null;
                if (ObjectUtil.isNull(stockDO.getLockQuantity()) || stockDO.getLockQuantity() == 0) {
                    stockDO.setLockQuantity(BigDecimal.ZERO.intValue());
                } else {
                    lockQuantity = NumberUtil.sub(stockDO.getLockQuantity(), actQuantity).intValue();
                    stockDO.setLockQuantity(lockQuantity);
                }
                // 设置可用库存
                if (convertFlag) {
                    stockDO.setLockQuantity(BigDecimal.ZERO.intValue());
                }
                Integer availableQuantity = stockDO.getAvailableQuantity() - actQuantity;
                stockDO.setAvailableQuantity(availableQuantity);
                Integer cabinetQuantity = Objects.isNull(stockDO.getCabinetQuantity())?0:stockDO.getCabinetQuantity();
                // 出库扣减拉柜数量
                if (StockTypeEnum.OUT_STOCK.getValue().equals(stockType)&&cabinetQuantity>0){
                    stockDO.setCabinetQuantity(cabinetQuantity-actQuantity);
                }
                //TODO 后续完善锁定库存处理逻辑
//                // 释放单据锁定库存
//                if (stockDO.getLockQuantity() == 0) {
//                    LambdaQueryWrapperX<StockLockDO> removeWapper = new LambdaQueryWrapperX<>();
//                    removeWapper.eq(StockLockDO::getBatchCode, batchCode);
//                    stockLockService.remove(removeWapper);
//                } else {
//                    LambdaUpdateWrapper<StockLockDO> updateWrapper = new LambdaUpdateWrapper<>();
//                    updateWrapper.eq(StockLockDO::getBatchCode, batchCode);
//                    updateWrapper.set(StockLockDO::getLockQuantity, lockQuantity);
//                    stockLockService.update(updateWrapper);
//                }
            } else {
                stockDO.setUsedQuantity(usedQuantity);
                //Integer availableQuantity = NumberUtil.sub(stockDO.getAvailableQuantity(), actQuantity, stockDO.getLockQuantity()).intValue();
                Integer availableQuantity = NumberUtil.sub(stockDO.getAvailableQuantity(), actQuantity).intValue();
                stockDO.setAvailableQuantity(availableQuantity);
            }

            if (stockDO.getAvailableQuantity() < 0) {
                throw exception(STOCK_EXCEED);
            }
        });
        // 更新采购合同明细出库数量
        purchaseContractApi.updatePurchaseItemOutQuantity(stockMap);
        List<StockDO> stockDOS = stockService.calculateRemainTotalAmount(stockDOList);
        return stockService.updateBatchById(stockDOS);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBill(Long id) {
        // 校验存在
        validateBillExists(id);
        // 删除
        boolean billFlag = billMapper.deleteById(id) > 0;

        // 删除子表
        boolean billItemFlag = deleteBillItemBySourceId(id);
        return billFlag && billItemFlag;
    }

    @Override
    public boolean deleteBillByContract(Long purchaseContractId) {
        List<Long> billIdList = billItemMapper.selectList(BillItemDO::getPurchaseContractId, purchaseContractId)
                .stream().map(BillItemDO::getSourceId).distinct().collect(Collectors.toList());

        List<BillDO> billDOS = billMapper.selectList(BillDO::getId, billIdList);
        if (CollectionUtils.isEmpty(billDOS)) {
            return false;
        }
        boolean billFlag = billMapper.deleteBatchIds(billIdList) > 0;

        LambdaQueryWrapper<BillItemDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(BillItemDO::getSourceId, billIdList);
        boolean billItemFlag = billItemMapper.delete(queryWrapper) > 0;

        return billFlag && billItemFlag;
    }

    private BillDO validateBillExists(Long id) {
        BillDO billDO = billMapper.selectById(id);
        if ( billDO== null) {
            throw exception(YSBILL_NOT_EXISTS);
        }
        return billDO;
    }

    @Override
    public BillRespVO getBill(Long id) {
        BillDO billDO = billMapper.selectById(id);
        if (billDO == null) {
            return null;
        }
        BillRespVO billRespVO = BillConvert.INSTANCE.convertBillRespVO(billDO);
        UserDept creatorInfo = adminUserApi.getUserDeptByUserId(Long.valueOf(billDO.getCreator()));
        billRespVO.setCreateUser(creatorInfo);
        List<BillItemDO> billItemDOS = billItemMapper.selectList(BillItemDO::getSourceId, id);
        if (!CollectionUtils.isEmpty(billItemDOS)) {
            List<Long> billItemSkuIdList = billItemDOS.stream().map(BillItemDO::getSkuId).collect(Collectors.toList());
            Map<Long, SkuDTO> skuDTOMap = skuApi.getSkuDTOMap(billItemSkuIdList);
            List<BillItemRespVO> billItemRespVOList = BeanUtils.toBean(billItemDOS, BillItemRespVO.class);
            billItemRespVOList.stream().forEach(x -> {
                SkuDTO skuDTO = skuDTOMap.get(x.getSkuId());
                String skuCode = "";
                SimpleFile simpleFile = new SimpleFile();
                if (ObjectUtil.isNotNull(skuDTO)) {
                    skuCode = skuDTO.getCode();
                    List<SimpleFile> pictureList = skuDTO.getPicture();
                    simpleFile = pictureList.stream().filter(picture -> BooleanEnum.YES.getValue().equals(picture.getMainFlag())).findFirst().orElse(null);

                }
                x.setSkuCode(skuCode);
                x.setMainPicture(simpleFile);
                // 补齐用户及部门信息
                AdminUserRespDTO purchaserUser = adminUserApi.getUser(x.getPurchaserId());
                x.setPurchaserName(purchaserUser != null ? purchaserUser.getNickname() : "");
                DeptRespDTO purchaserDept = deptApi.getDept(x.getPurchaserDeptId());
                x.setPurchaserDeptName(purchaserDept != null ? purchaserDept.getName() : "");

            });
            billRespVO.setChildren(billItemRespVOList);
        }

        return billRespVO;
    }

    @Override
    public PageResult<BillRespVO> getBillPage(BillPageReqVO pageReqVO) {
        if (StrUtil.isNotEmpty(pageReqVO.getBasicSkuCode()) || StrUtil.isNotEmpty(pageReqVO.getCskuCode()) || StrUtil.isNotEmpty(pageReqVO.getSkuCode()) || Objects.nonNull(pageReqVO.getPurchaserId()) || Objects.nonNull(pageReqVO.getPurchaserDeptId()) || Objects.nonNull(pageReqVO.getVenderName())|| Objects.nonNull(pageReqVO.getPurchaseContractCode()) || Objects.nonNull(pageReqVO.getSalesId())){
            LambdaQueryWrapperX queryWrapperX = new LambdaQueryWrapperX<BillItemDO>().
                    likeIfPresent(BillItemDO::getCskuCode, pageReqVO.getCskuCode()).
                    likeIfPresent(BillItemDO::getBasicSkuCode, pageReqVO.getBasicSkuCode()).
                    likeIfPresent(BillItemDO::getSkuCode, pageReqVO.getSkuCode())
                    .eqIfPresent(BillItemDO::getPurchaserId,pageReqVO.getPurchaserId())
                    .eqIfPresent(BillItemDO::getPurchaserDeptId,pageReqVO.getPurchaserDeptId())
                    .likeIfPresent(BillItemDO::getVenderName, pageReqVO.getVenderName())
                    .likeIfPresent(BillItemDO::getPurchaseContractCode, pageReqVO.getPurchaseContractCode());
            if(Objects.nonNull(pageReqVO.getSalesId())){
                queryWrapperX.apply("JSON_EXTRACT(sales, '$.userId') = {0}", pageReqVO.getSalesId());
            }
            if(Objects.nonNull(pageReqVO.getManagerId())){
                queryWrapperX.apply("JSON_EXTRACT(manager, '$.userId') = {0}", pageReqVO.getManagerId());
            }
            List<BillItemDO> billItemDOList = billItemMapper.selectList(queryWrapperX);
            if (CollUtil.isEmpty(billItemDOList)) {
                return PageResult.empty();
            }
            pageReqVO.setIdList(billItemDOList.stream().map(BillItemDO::getSourceId).distinct().toArray(Long[]::new));
        }
        PageResult<BillDO> billDOPageResult = billMapper.selectPage(pageReqVO);
        List<BillDO> list = billDOPageResult.getList();
        if (CollectionUtils.isEmpty(list)) {
            return new PageResult<BillRespVO>().setList(new ArrayList<>()).setTotal(billDOPageResult.getTotal());
        }
        // 查询出（入）库单子表信息，并根据出入库单据主键进行分组
        List<Long> idList = list.stream().map(BillDO::getId).collect(Collectors.toList());
        List<BillItemDO> billItemDOS = this.getBillItemListBySourceId(idList);
        Map<Long, List<BillItemRespVO>> collectBillItemMap;
        if (!CollectionUtils.isEmpty(billItemDOS)) {
            List<Long> billItemSkuIdList = billItemDOS.stream().map(BillItemDO::getSkuId).collect(Collectors.toList());
            Map<Long, SkuDTO> skuDTOMap = skuApi.getSkuDTOMap(billItemSkuIdList);
            List<BillItemRespVO> billItemRespVOList = BeanUtils.toBean(billItemDOS, BillItemRespVO.class);

            List<Long> purchaserIdList = billItemRespVOList.stream().map(BillItemRespVO::getPurchaserId).distinct().toList();
            Map<Long, AdminUserRespDTO> purchaserMap = adminUserApi.getUserMap(purchaserIdList);
            List<Long> purchaserDeptIdList = billItemRespVOList.stream().map(BillItemRespVO::getPurchaserDeptId).distinct().toList();
            Map<Long, DeptRespDTO> purchaserDeptMap = deptApi.getDeptMap(purchaserDeptIdList);

            billItemRespVOList.stream().forEach(x -> {
                SkuDTO skuDTO = skuDTOMap.get(x.getSkuId());
                String skuCode = "";
                SimpleFile simpleFile = new SimpleFile().setFileUrl("");
                if (ObjectUtil.isNotNull(skuDTO)) {
                    skuCode = skuDTO.getCode();
                    List<SimpleFile> pictureList = skuDTO.getPicture();
                    simpleFile = pictureList.stream().filter(picture -> BooleanEnum.YES.getValue().equals(picture.getMainFlag())).findFirst().orElse(null);
                    x.setThumbnail(skuDTO.getThumbnail());
                }
                x.setSkuCode(skuCode);
                x.setMainPicture(simpleFile);

                AdminUserRespDTO purchaserUser = purchaserMap.get(x.getPurchaserId());
                x.setPurchaserName(purchaserUser != null ? purchaserUser.getNickname() : "");
                DeptRespDTO purchaserDeptRespDTO = purchaserDeptMap.get(x.getPurchaserDeptId());
                x.setPurchaserDeptName(purchaserDeptRespDTO != null ? purchaserDeptRespDTO.getName() : "");
            });
            collectBillItemMap = billItemRespVOList.stream().collect(Collectors.groupingBy(BillItemRespVO::getSourceId));
        } else {
            collectBillItemMap = new HashMap<>();
        }
        // 补齐用户及部门信息
        List<BillRespVO> billRespList = BeanUtils.toBean(list, BillRespVO.class);
        Map<Long, UserDept> userDeptCache = adminUserApi.getUserDeptCache(null);
        if (!CollectionUtils.isEmpty(billRespList)) {
            billRespList.forEach(x -> {
                UserDept creatorUser = userDeptCache.get(Long.valueOf(x.getCreator()));
                x.setCreatorName(creatorUser != null ? creatorUser.getNickname() : "");
                x.setCreatorDeptName(creatorUser != null ? creatorUser.getDeptName() : "");
                x.setChildren(collectBillItemMap.get(x.getId()));
            });
        }
        return new PageResult<BillRespVO>().setList(billRespList).setTotal(billDOPageResult.getTotal());
    }

    // ==================== 子表（仓储管理-入(出)库单-明细） ====================

    @Override
    public List<BillItemDO> getBillItemListBySourceId(List<Long> sourceIdList) {
        return billItemMapper.selectList(BillItemDO::getSourceId, sourceIdList);
    }

    public boolean updateBillItemList(Long sourceId, String sourceCode, Integer billtype, List<BillItemDO> billItemDOList, boolean updateStatusFlag) {
        deleteBillItemBySourceId(sourceId);
        billItemDOList.forEach(o -> {
            String batchCode = o.getBatchCode();
            batchCode = StrUtil.isEmpty(batchCode) ? batchCodeUtil.genBatchCode(CommonDict.EMPTY_STR) : batchCode;
            o.setBatchCode(batchCode);
            o.setSourceId(sourceId).setSourceCode(sourceCode).setBillType(billtype);
            // 解决更新情况下：1）id 冲突；2）updateTime 不更新
            o.setId(null).setUpdater(null).setUpdateTime(null);
            if (Objects.nonNull(o.getPurchaserId())) {
                UserDept userDept = adminUserApi.getUserDeptByUserId(o.getPurchaserId());
                if (Objects.nonNull(userDept)) {
                    o.setPurchaserDeptId(userDept.getDeptId());
                }
            }
            // 更新入库状态
            if (updateStatusFlag) {
                int actQuantity = Objects.isNull(o.getActQuantity()) ? CalculationDict.ZERO : o.getActQuantity();
                if (actQuantity > 0) {
                    if (actQuantity >= o.getOrderQuantity()) {
                        o.setNoticeItemStatus(BillStatusEnum.BILLED.getStatus());
                    } else {
                        o.setNoticeItemStatus(BillStatusEnum.PART_BILL.getStatus());
                    }
                } else {
                    o.setNoticeItemStatus(BillStatusEnum.NOT_BILL.getStatus());
                }
            }
        });
        return billItemMapper.insertBatch(billItemDOList);
    }

    @Override
    public List<Long> getIdListByContractId(Long purchaseContractId) {
        List<Long> billIdList = billItemMapper.selectList(BillItemDO::getPurchaseContractId, purchaseContractId)
                .stream().map(BillItemDO::getSourceId).distinct().collect(Collectors.toList());
        return billIdList;
    }

    @Override
    public Long getBillNumByPurchaseId(Long id) {
        List<Long> billIdList = billItemMapper.selectList(BillItemDO::getPurchaseContractId, id)
                .stream().map(BillItemDO::getSourceId).distinct().collect(Collectors.toList());
        return Long.valueOf(billIdList.size());
    }

    @Override
    public boolean checkBillByNoticeCode(String noticeCode) {
        return billMapper.selectCount(new LambdaQueryWrapper<BillDO>().eq(BillDO::getNoticeCode, noticeCode)) > CalculationDict.ZERO;
    }

    @Override
    public List<BillItemDO> getBillItemByPurchaseContractCode(String purchaseContractCode, Set<String> skuCodes) {
        if (StrUtil.isEmpty(purchaseContractCode) || CollUtil.isEmpty(skuCodes)) {
            return List.of();
        }
        return billItemMapper.selectList(new LambdaQueryWrapper<BillItemDO>().eq(BillItemDO::getPurchaseContractCode, purchaseContractCode).in(BillItemDO::getSkuCode, skuCodes));
    }

    @Override
    public void updateBillItemList(List<BillItemDO> billItemDOList) {
        if (CollUtil.isEmpty(billItemDOList)) {
            return;
        }
        billItemMapper.updateBatch(billItemDOList);
    }

    @Override
    public List<BillItemDO> getBillItemListByNoticeCodeList(List<String> noticeCodeList) {
        if (CollUtil.isEmpty(noticeCodeList)){
            return List.of();
        }
        List<BillDO> billDOS = billMapper.selectList(new LambdaQueryWrapper<BillDO>().in(BillDO::getNoticeCode, noticeCodeList).eq(BillDO::getBillStatus,StockBillStatusEnum.UN_CONFIRM.getValue()));
        if (CollUtil.isEmpty(billDOS)){
            return List.of();
        }
        return billItemMapper.selectList(BillItemDO::getSourceId, billDOS.stream().map(BillDO::getId).toList());
    }

    @Override
    public void genInternalContract(Long billId) {
        BillDO billDO = validateBillExists(billId);
        List<BillItemDO> billItemDOS = billItemMapper.selectList(BillItemDO::getSourceId, billId);
        if (CollUtil.isEmpty(billItemDOS)){
            return;
        }
        String oldGenUniqueCode = billDO.getGenContractUniqueCode();
        if (StrUtil.isNotEmpty(oldGenUniqueCode)){
            saleContractApi.deleteGenContract(oldGenUniqueCode);
            purchaseContractApi.deleteGenContract(oldGenUniqueCode);
        }
        String genContractUniqueCode = IdUtil.fastSimpleUUID();
        Map<String, Integer> itemMap = billItemDOS.stream().collect(Collectors.groupingBy(BillItemDO::getSaleItemUniqueCode, Collectors.summingInt(BillItemDO::getActQuantity)));
        saleContractApi.genInternalContractByOutBill(itemMap,genContractUniqueCode);
    }

    @Override
    public Map<String, List<ShipmentPriceEntity>> getStockPriceBySaleContractCode(List<String> saleContractCodeList) {
        if (CollUtil.isEmpty(saleContractCodeList)){
            return Map.of();
        }
        List<BillDO> billDOS = billMapper.selectList(BillDO::getSaleContractCode, saleContractCodeList);
        if (CollUtil.isEmpty(billDOS)){
            return Map.of();
        }
        List<Long> billIdList = billDOS.stream().map(BillDO::getId).distinct().collect(Collectors.toList());
        List<BillItemDO> billItemDOS = billItemMapper.selectList(BillItemDO::getSourceId, billIdList);
        if (CollUtil.isEmpty(billItemDOS)){
            return Map.of();
        }
        Map<Long, List<BillItemDO>> billItemMap = billItemDOS.stream().collect(Collectors.groupingBy(BillItemDO::getSourceId));
        List<String> batchCodeList = billItemDOS.stream().map(BillItemDO::getBatchCode).distinct().collect(Collectors.toList());
        Map<String, StockDO> stockListByBatchCode = stockService.getStockListByBatchCode(batchCodeList);
        if (CollUtil.isEmpty(stockListByBatchCode)){
            return Map.of();
        }
        Map<String, List<ShipmentPriceEntity>> result = new HashMap<>();
        billDOS.stream().collect(Collectors.groupingBy(BillDO::getSaleContractCode)).forEach((saleContractCode,billDOList)->{
            List<ShipmentPriceEntity> shipmentPriceEntityList = new ArrayList<>();
            billDOList.forEach(billDO -> {
                JsonAmount receivablePrice = new JsonAmount();
                List<BillItemDO> billItemDOList = billItemMap.get(billDO.getId());
                if (CollUtil.isEmpty(billItemDOList)){
                    return;
                }
                billItemDOList.forEach(billItemDO -> {
                    StockDO stockDO = stockListByBatchCode.get(billItemDO.getBatchCode());
                    if (Objects.isNull(stockDO)){
                        return;
                    }
                    JsonAmount price = stockDO.getPrice();
                    Integer actQuantity = billItemDO.getActQuantity();
                    receivablePrice.add(price.mul(BigDecimal.valueOf(actQuantity)));
                });
                ShipmentPriceEntity shipmentPriceEntity = new ShipmentPriceEntity();
                shipmentPriceEntity.setInvoiceCode(billDO.getCode());
                shipmentPriceEntity.setReceivablePrice(receivablePrice);
                shipmentPriceEntityList.add(shipmentPriceEntity);
            });
            result.put(saleContractCode,shipmentPriceEntityList);
        });
        return result;
    }

    @Override
    public Map<String, LocalDateTime> getOutStockTimeBySaleItems(List<String> saleItemUniqueCodeList) {
       if (CollUtil.isEmpty(saleItemUniqueCodeList)){
           return Map.of();
       }
        List<BillItemDO> billItemDOS = billItemMapper.selectList(new LambdaQueryWrapper<BillItemDO>().select(BillItemDO::getSourceId,BillItemDO::getSaleItemUniqueCode).eq(BillItemDO::getBillType, StockTypeEnum.OUT_STOCK.getValue()));
       if (CollUtil.isEmpty(billItemDOS)){
           return Map.of();
       }
        List<Long> billIdList = billItemDOS.stream().map(BillItemDO::getSourceId).distinct().toList();
        List<BillDO> billDOS = billMapper.selectList(new LambdaQueryWrapper<BillDO>().select(BillDO::getBillTime,BillDO::getId).in(BillDO::getId, billIdList).eq(BillDO::getBillStatus, StockBillStatusEnum.CONFIRMED.getValue()));
        if (CollUtil.isEmpty(billDOS)){
            return Map.of();
        }
        Map<Long, LocalDateTime> billMap = billDOS.stream().collect(Collectors.toMap(BillDO::getId, BillDO::getBillTime));
        Map<String, LocalDateTime> result = new HashMap<>();
        billItemDOS.forEach(s->{
            LocalDateTime localDateTime = billMap.get(s.getSourceId());
            result.put(s.getSaleItemUniqueCode(),localDateTime);
        });
        return result;
    }

    private boolean deleteBillItemBySourceId(Long sourceId) {
        LambdaQueryWrapper<BillItemDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BillItemDO::getSourceId, sourceId);
        return billItemMapper.delete(queryWrapper) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean cancel(Long id) {
        // 0. 校验数据
        this.validateBillExists(id);
        BillRespVO billRespVO = this.getBill(id);
        List<BillItemRespVO> billItemRespVOList = billRespVO.getChildren();
        if (CollectionUtils.isEmpty(billItemRespVOList)) {
            return false;
        }
        List<Long> billItemIdList = billItemRespVOList.stream().map(BillItemRespVO::getId).collect(Collectors.toList());

        // 校验库存明细是否已被使用或锁定
        LambdaQueryWrapper<StockDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(StockDO::getBillItemId, billItemIdList);
        List<StockDO> stockDOList = stockService.list(queryWrapper);
        // 校验库存未占用、未使用
        StringBuilder errStr = new StringBuilder();
        stockDOList.stream().forEach(x -> {
            // 出库数量
            Integer usedQuantity = x.getUsedQuantity();
            if (usedQuantity.intValue() != 0) {
                errStr.append("产品：" + x.getSkuName() + "，库存已被使用，不可反提交/t");
            }
            // 校验锁定库存
            Integer lockQuantity = x.getLockQuantity();
            if (lockQuantity.intValue() != 0) {
                errStr.append("产品：" + x.getSkuName() + "，库存已被锁定，不可反提交/t");
            }
            x.setUsedQuantity(BigDecimal.ZERO.intValue());
            x.setLockQuantity(BigDecimal.ZERO.intValue());
        });
        if (StrUtil.isNotBlank(errStr)) {
            throw exception(new ErrorCode(500, String.valueOf(errStr)));
        }

        // 1. 移除泛太入库批次库存信息
        List<Long> stockDOIdList = stockDOList.stream().map(StockDO::getId).collect(Collectors.toList());
        boolean stockFlag = stockService.removeBatchByIds(stockDOIdList);

        // 查询供应商批次及出库库存
        List<BillItemDO> billItemDOList = billItemMapper.selectBatchIds(billItemIdList);
        if (!CollectionUtils.isEmpty(billItemDOList)) {
            List<String> uniqueCodeList = billItemDOList.stream().map(BillItemDO::getUniqueCode).toList();
            LambdaQueryWrapper<BillItemDO> venderOutBillItemDOQuery = new LambdaQueryWrapper<>();
            venderOutBillItemDOQuery.in(BillItemDO::getUniqueCode, uniqueCodeList);
            venderOutBillItemDOQuery.eq(BillItemDO::getBillType, StockTypeEnum.OUT_STOCK.getValue());
            List<BillItemDO> venderOutBillItemDOList = billItemMapper.selectList(venderOutBillItemDOQuery);
            if (!CollectionUtils.isEmpty(venderOutBillItemDOList)) {
                // 移除供应商出库库存
                List<Long> venderOutBillItemIdList = venderOutBillItemDOList.stream().map(BillItemDO::getId).toList();
                billItemMapper.deleteBatchIds(venderOutBillItemIdList);
                List<Long> venderOutBillIdList = venderOutBillItemDOList.stream().map(BillItemDO::getSourceId).toList();
                billMapper.deleteBatchIds(venderOutBillIdList);

                // 查询供应商入库批次信息，并进行修改
                List<String> batchCodeList = venderOutBillItemDOList.stream().map(BillItemDO::getBatchCode).toList();
                LambdaQueryWrapper<StockDO> venderInStockQuery = new LambdaQueryWrapper<>();
                venderInStockQuery.in(StockDO::getBatchCode, batchCodeList);
                List<StockDO> venderInStockList = stockService.list(venderInStockQuery);
                if (!CollectionUtils.isEmpty(venderInStockList)) {
                    venderInStockList.forEach(x -> x.setLockQuantity(BigDecimal.ZERO.intValue()).setAvailableQuantity(x.getInitQuantity()).setUsedQuantity(BigDecimal.ZERO.intValue()));
                    stockService.updateBatchById(venderInStockList);
                }
            }
        }

        //  2. 作废入(出)库单、删除入(出)库单明细
        BillDO billDO = BeanUtils.toBean(billRespVO, BillDO.class);
        billDO.setBillStatus(StockBillStatusEnum.CANCEL.getValue());
        boolean billFlag = super.updateById(billDO);
        boolean billItemFlag = billItemMapper.deleteBatchIds(billItemIdList) == billItemIdList.size();

        //  3. 更新入(出)库通知单数据
        LambdaQueryWrapper<StockNoticeDO> noticeQuery = new LambdaQueryWrapper<>();
        noticeQuery.eq(StockNoticeDO::getCode, billRespVO.getNoticeCode());
        StockNoticeDO stockNoticeDO = stockNoticeService.getOne(noticeQuery);
        boolean noticeFlag = false;
        if (ObjectUtil.isNotNull(stockNoticeDO)) {
            stockNoticeDO.setNoticeStatus(NoticeStatusEnum.UN_CONVERT.getValue());
            noticeFlag = stockNoticeService.updateById(stockNoticeDO);
        } else {
            noticeFlag = true;
        }
        // 更新通知单数量
        updateNoticeQuantity(BillItemConvert.INSTANCE.convertBillItemDOList(billItemRespVOList), false, true);
        return stockFlag && billFlag && billItemFlag && noticeFlag;
    }

    @Override
    public List<BillItemRespVO> getRecord(String batchCode) {
        LambdaQueryWrapper<BillItemDO> query = new LambdaQueryWrapper<>();
        query.eq(BillItemDO::getBatchCode, batchCode);
        List<BillItemDO> billItemDOS = billItemMapper.selectList(query);

        List<BillItemRespVO> billItemRespVOList = BeanUtils.toBean(billItemDOS, BillItemRespVO.class);
        if (!CollectionUtils.isEmpty(billItemRespVOList)) {
            // 过滤未提交的出入库单
            List<Long> unConfirmBillIdList = new ArrayList<>();
            List<Long> billIdList = billItemRespVOList.stream().map(BillItemRespVO::getSourceId).toList();
            List<BillDO> billDOList = billMapper.selectBatchIds(billIdList);
            if (!CollectionUtils.isEmpty(billDOList)) {
                unConfirmBillIdList = billDOList.stream().filter(x -> x.getBillStatus().intValue() == StockBillStatusEnum.UN_CONFIRM.getValue()).map(BillDO::getId).toList();
            }
            if (!CollectionUtils.isEmpty(unConfirmBillIdList)) {
                List<Long> finalUnConfirmBillIdList = unConfirmBillIdList;
                billItemRespVOList = billItemRespVOList.stream().filter(x -> !finalUnConfirmBillIdList.contains(x.getSourceId())).toList();
            }
            List<Long> billItemSkuIdList = billItemRespVOList.stream().map(BillItemRespVO::getSkuId).collect(Collectors.toList());
            Map<Long, SkuDTO> skuDTOMap = skuApi.getSkuDTOMap(billItemSkuIdList);

            List<Long> creatorList = billItemRespVOList.stream().map(x -> Long.valueOf(x.getCreator())).distinct().collect(Collectors.toList());
            Map<Long, AdminUserRespDTO> creatorMap = adminUserApi.getUserMap(creatorList);
            billItemRespVOList.stream().forEach(x -> {
                SkuDTO skuDTO = skuDTOMap.get(x.getSkuId());
                String skuCode = "";
                SimpleFile simpleFile = new SimpleFile().setFileUrl("");
                if (ObjectUtil.isNotNull(skuDTO)) {
                    skuCode = skuDTO.getCode();
                    List<SimpleFile> pictureList = skuDTO.getPicture();
                    simpleFile = pictureList.stream().filter(picture -> BooleanEnum.YES.getValue().equals(picture.getMainFlag())).findFirst().orElse(null);
                }
                x.setSkuCode(skuCode);
                x.setMainPicture(simpleFile);

                AdminUserRespDTO creatorUser = creatorMap.get(Long.valueOf(x.getCreator()));
                x.setCreatorName(creatorUser != null ? creatorUser.getNickname() : "");
            });
        }
        return billItemRespVOList;
    }
}
