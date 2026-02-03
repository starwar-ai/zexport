package com.syj.eplus.module.sms.convert.salecontract;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.dict.CommonDict;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import com.syj.eplus.framework.common.entity.BatchChildVO;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.StockChildVO;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.framework.common.util.NumUtil;
import com.syj.eplus.framework.common.util.NumberFormatUtil;
import com.syj.eplus.framework.common.util.TransformUtil;
import com.syj.eplus.module.dms.enums.api.dto.SimpleShipmentItemDTO;
import com.syj.eplus.module.pms.api.packageType.dto.PackageTypeDTO;
import com.syj.eplus.module.scm.api.purchasecontract.dto.SavePurchaseContractItemReqVO;
import com.syj.eplus.module.scm.api.purchasecontract.dto.SimplePurchaseContractItemDTO;
import com.syj.eplus.module.sms.api.dto.SaleContractItemDTO;
import com.syj.eplus.module.sms.controller.admin.salecontract.vo.*;
import com.syj.eplus.module.sms.dal.dataobject.addsubitem.AddSubItem;
import com.syj.eplus.module.sms.dal.dataobject.collectionplan.CollectionPlan;
import com.syj.eplus.module.sms.dal.dataobject.salecontract.SaleContractDO;
import com.syj.eplus.module.sms.dal.dataobject.salecontractchange.SaleContractChange;
import com.syj.eplus.module.sms.dal.dataobject.salecontractitem.SaleContractItem;
import com.syj.eplus.module.sms.util.CalcContractPurchaseUtil;
import com.syj.eplus.module.wms.api.stock.dto.StockDTO;
import com.syj.eplus.module.wms.api.stock.dto.StockLockRespVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.sms.enums.ErrorCodeConstants.CURRENCY_RATE_NOT_EXISTS;


@Mapper
public interface SaleContractConvert {

    SaleContractConvert INSTANCE = Mappers.getMapper(SaleContractConvert.class);

    SaleContractRespVO convert(SaleContractDO saleContractDO);

    SaleContractSimpleRespVO convert1(SaleContractDO saleContractDO);

    default SaleContractRespVO convertSaleContractRespVO(SaleContractDO saleContractDO) {
        return convert(saleContractDO);
    }

    SaleContractDO convertSaleContractDO(SaleContractSaveReqVO saveReqVO);

    /**
     * 单据维度导出转换（不含明细数据）
     * 用于单据维度导出时，跳过明细数据查询，提升导出性能
     * 
     * @param saleContractDOList 合同主表列表
     * @return 导出VO列表
     * @author 波波
     */
    default List<SaleContractRespVO> convertSaleContractRespVODocumentExport(List<SaleContractDO> saleContractDOList) {
        return CollectionUtils.convertList(saleContractDOList, saleContractDO -> {
            SaleContractRespVO saleContractRespVO = convert(saleContractDO);
            // 填充金额拆分字段（包括销售总金额、销售总金额币种、销售总金额USD）
            fillAmountSplitFields(saleContractRespVO);
            return saleContractRespVO;
        });
    }

    /**
     * 产品维度导出转换（包含明细数据）
     * 
     * @param saleContractDOList 合同主表列表
     * @param contractItemMap 合同明细映射
     * @return 导出VO列表
     * @author 波波
     */
    default List<SaleContractRespVO> convertSaleContractRespVOExport(List<SaleContractDO> saleContractDOList, Map<Long, List<SaleContractItem>> contractItemMap) {
        return CollectionUtils.convertList(saleContractDOList, saleContractDO -> {
            Long contractId = saleContractDO.getId();
            SaleContractRespVO saleContractRespVO = convert(saleContractDO);
            if (CollUtil.isNotEmpty(contractItemMap)) {
                saleContractRespVO.setChildren(contractItemMap.get(contractId));
            }
            // 填充金额拆分字段
            fillAmountSplitFields(saleContractRespVO);
            return saleContractRespVO;
        });
    }

    /**
     * 单据模式简化转换（只处理主表数据，不查询采购/出运信息）
     * 用于单据视图列表查询，提升响应速度
     * 
     * 优化说明：
     * - 不查询子明细的采购信息、出运信息
     * - 只转换主表数据和收款计划
     * - 收款金额在 Service 层单独计算
     *
     * @param saleContractDOList 合同主表列表
     * @param collectionPlanMap  收款计划映射（用于计算已收/未收金额）
     * @return 单据模式VO列表
     * @author 波波
     */
    default List<SaleContractRespVO> convertSaleContractRespVOSimple(List<SaleContractDO> saleContractDOList,
                                                                     Map<Long, List<CollectionPlan>> collectionPlanMap) {
        return CollectionUtils.convertList(saleContractDOList, saleContractDO -> {
            Long contractId = saleContractDO.getId();
            SaleContractRespVO saleContractRespVO = convert(saleContractDO);
            // 设置收款计划（用于后续计算收款金额）
            if (CollUtil.isNotEmpty(collectionPlanMap)) {
                saleContractRespVO.setCollectionPlanList(collectionPlanMap.get(contractId));
            }
            // 填充金额拆分字段
            fillAmountSplitFields(saleContractRespVO);
            return saleContractRespVO;
        });
    }

    default List<SaleContractRespVO> convertSaleContractRespVO(List<SaleContractDO> saleContractDOList, Map<Long, List<SaleContractItem>> contractItemMap,
                                                               Map<String, List<AddSubItem>> addsubItemMap, Map<Long, List<CollectionPlan>> collectionPlanMap,
                                                               List<PackageTypeDTO> packageTypeList, Map<String, String> configMap, Map<String,Map<String, BigDecimal>> currencyRateMap,
                                                               Map<Long, List<SimplePurchaseContractItemDTO>> simplePurchaseContractItemMap,Map<Long, SimpleShipmentItemDTO> simpleShipmentItemDTOMap) {
        return CollectionUtils.convertList(saleContractDOList, saleContractDO -> {
            Long contractId = saleContractDO.getId();
            String code = saleContractDO.getCode();
            List<SaleContractItem> saleContractItemList = contractItemMap.get(contractId);
            List<AddSubItem> addSubItems = Objects.isNull(addsubItemMap)?new ArrayList<>():addsubItemMap.get(code);
            // 计算采购信息
            transformPurchaseMsg(saleContractItemList,simplePurchaseContractItemMap,currencyRateMap,simpleShipmentItemDTOMap);
            CalcContractPurchaseUtil.CalcContractPurchase(saleContractDO, configMap,currencyRateMap.get(saleContractDO.getCode()),saleContractItemList, addSubItems);
            SaleContractRespVO saleContractRespVO = convert(saleContractDO);
            if (CollUtil.isNotEmpty(contractItemMap)) {
                if (CollUtil.isNotEmpty(saleContractItemList)) {
                    saleContractItemList.forEach(s -> {
                        Long lastCompanyId = TransformUtil.getLastCompanyId(saleContractDO.getCompanyPath());
                        if (Objects.nonNull(lastCompanyId)){
                            s.setCompanyIdList(List.of(lastCompanyId));
                        }
                        s.setCompanyId(saleContractDO.getCompanyId());
                        s.setCompanyName(saleContractDO.getCompanyName());
                        if(CollUtil.isNotEmpty(packageTypeList) && CollUtil.isNotEmpty(s.getPackageType())){
                            List<Long> distinctPackgeType = s.getPackageType().stream().distinct().toList();
                            List<PackageTypeDTO> tempPackageTypeList = packageTypeList.stream().filter(item->distinctPackgeType.contains(item.getId())).toList();
                            if(CollUtil.isNotEmpty(tempPackageTypeList)){
                                List<String> packageTypeNameList = tempPackageTypeList.stream().map(PackageTypeDTO::getName).distinct().toList();
                                s.setPackageTypeName(String.join(",",packageTypeNameList));
                                List<String> packageTypeNameEngList = tempPackageTypeList.stream().map(PackageTypeDTO::getNameEng).distinct().toList();
                                s.setPackageTypeEngName(String.join(",",packageTypeNameEngList));
                            }
                        }
                    });

                    saleContractRespVO.setChildren(saleContractItemList);
                }

            }
            if (CollUtil.isNotEmpty(addsubItemMap)) {
                saleContractRespVO.setAddSubItemList(addsubItemMap.get(code));
            }
            if (CollUtil.isNotEmpty(collectionPlanMap)) {
                List<CollectionPlan> collectionPlanList = collectionPlanMap.get(contractId);
                if (CollUtil.isNotEmpty(collectionPlanList)) {
                    // 获取币种
                    Optional<String> currencyOptional = collectionPlanList.stream()
                            .map(CollectionPlan::getReceivableAmount)
                            .filter(Objects::nonNull)
                            .map(s -> {
                                if (StrUtil.isEmpty(s.getCurrency())) {
                                    return CommonDict.EMPTY_STR;
                                }
                                return s.getCurrency();
                            })
                            .findFirst();
                    String currency = currencyOptional.orElse("");
                    // 计算应收总额
                    BigDecimal totalReceivableAmount = collectionPlanList.stream()
                            .map(CollectionPlan::getReceivableAmount)
                            .map(JsonAmount::getAmount)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    // 计算实收总额
                    BigDecimal totalReceivedAmount = collectionPlanList.stream()
                            .map(CollectionPlan::getReceivedAmount)
                            .map(JsonAmount::getAmount)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    saleContractRespVO.setReceivableAmount(new JsonAmount().setAmount(totalReceivableAmount).setCurrency(currency));
                    saleContractRespVO.setReceivedAmount(new JsonAmount().setAmount(totalReceivedAmount).setCurrency(currency));
                }
                saleContractRespVO.setCollectionPlanList(collectionPlanList);
            }
            // 填充金额拆分字段
            fillAmountSplitFields(saleContractRespVO);
            return saleContractRespVO;
        });
    }

    /**
     * 填充金额拆分字段数据
     * @param saleContractRespVO 销售合同响应VO
     */
    default void fillAmountSplitFields(SaleContractRespVO saleContractRespVO) {
        if (saleContractRespVO == null) {
            return;
        }

        // 拖柜费拆分
        if (saleContractRespVO.getTrailerFee() != null) {
            saleContractRespVO.setTrailerFeeAmount(NumberFormatUtil.formatAmount(saleContractRespVO.getTrailerFee().getAmount()));
            saleContractRespVO.setTrailerFeeCurrency(saleContractRespVO.getTrailerFee().getCurrency());
        }

        // 预估总运费拆分
        if (saleContractRespVO.getEstimatedTotalFreight() != null) {
            saleContractRespVO.setEstimatedTotalFreightAmount(NumberFormatUtil.formatAmount(saleContractRespVO.getEstimatedTotalFreight().getAmount()));
            saleContractRespVO.setEstimatedTotalFreightCurrency(saleContractRespVO.getEstimatedTotalFreight().getCurrency());
        }

        // 佣金拆分
        if (saleContractRespVO.getCommission() != null) {
            saleContractRespVO.setCommissionAmount(NumberFormatUtil.formatAmount(saleContractRespVO.getCommission().getAmount()));
            saleContractRespVO.setCommissionCurrency(saleContractRespVO.getCommission().getCurrency());
        }

        // 平台费拆分
        if (saleContractRespVO.getPlatformFee() != null) {
            saleContractRespVO.setPlatformFeeAmount(NumberFormatUtil.formatAmount(saleContractRespVO.getPlatformFee().getAmount()));
            saleContractRespVO.setPlatformFeeCurrency(saleContractRespVO.getPlatformFee().getCurrency());
        }

        // 保险费拆分
        if (saleContractRespVO.getInsuranceFee() != null) {
            saleContractRespVO.setInsuranceFeeAmount(NumberFormatUtil.formatAmount(saleContractRespVO.getInsuranceFee().getAmount()));
            saleContractRespVO.setInsuranceFeeCurrency(saleContractRespVO.getInsuranceFee().getCurrency());
        }

        // 中信保费用拆分
        if (saleContractRespVO.getSinosureFee() != null) {
            saleContractRespVO.setSinosureFeeAmount(NumberFormatUtil.formatAmount(saleContractRespVO.getSinosureFee().getAmount()));
            saleContractRespVO.setSinosureFeeCurrency(saleContractRespVO.getSinosureFee().getCurrency());
        }

        // 包干费拆分
        if (saleContractRespVO.getLumpSumFee() != null) {
            saleContractRespVO.setLumpSumFeeAmount(NumberFormatUtil.formatAmount(saleContractRespVO.getLumpSumFee().getAmount()));
            saleContractRespVO.setLumpSumFeeCurrency(saleContractRespVO.getLumpSumFee().getCurrency());
        }

        // 加项金额拆分
        if (saleContractRespVO.getAdditionAmount() != null) {
            saleContractRespVO.setAdditionAmountAmount(NumberFormatUtil.formatAmount(saleContractRespVO.getAdditionAmount().getAmount()));
            saleContractRespVO.setAdditionAmountCurrency(saleContractRespVO.getAdditionAmount().getCurrency());
        }

        // 减项金额拆分
        if (saleContractRespVO.getDeductionAmount() != null) {
            saleContractRespVO.setDeductionAmountAmount(NumberFormatUtil.formatAmount(saleContractRespVO.getDeductionAmount().getAmount()));
            saleContractRespVO.setDeductionAmountCurrency(saleContractRespVO.getDeductionAmount().getCurrency());
        }

        // 验货费用拆分
        if (saleContractRespVO.getInspectionFee() != null) {
            saleContractRespVO.setInspectionFeeAmount(NumberFormatUtil.formatAmount(saleContractRespVO.getInspectionFee().getAmount()));
            saleContractRespVO.setInspectionFeeCurrency(saleContractRespVO.getInspectionFee().getCurrency());
        }

        // 预计包材合计拆分
        if (saleContractRespVO.getEstimatedPackingMaterials() != null) {
            saleContractRespVO.setEstimatedPackingMaterialsAmount(NumberFormatUtil.formatAmount(saleContractRespVO.getEstimatedPackingMaterials().getAmount()));
            saleContractRespVO.setEstimatedPackingMaterialsCurrency(saleContractRespVO.getEstimatedPackingMaterials().getCurrency());
        }

        // 配件采购合计拆分
        if (saleContractRespVO.getAccessoriesPurchaseTotal() != null) {
            saleContractRespVO.setAccessoriesPurchaseTotalAmount(NumberFormatUtil.formatAmount(saleContractRespVO.getAccessoriesPurchaseTotal().getAmount()));
            saleContractRespVO.setAccessoriesPurchaseTotalCurrency(saleContractRespVO.getAccessoriesPurchaseTotal().getCurrency());
        }

        // 货值合计拆分
        if (saleContractRespVO.getTotalGoodsValue() != null) {
            saleContractRespVO.setTotalGoodsValueAmount(NumberFormatUtil.formatAmount(saleContractRespVO.getTotalGoodsValue().getAmount()));
            saleContractRespVO.setTotalGoodsValueCurrency(saleContractRespVO.getTotalGoodsValue().getCurrency());
        }

        // 退税合计拆分
        if (saleContractRespVO.getTotalVatRefund() != null) {
            saleContractRespVO.setTotalVatRefundAmount(NumberFormatUtil.formatAmount(saleContractRespVO.getTotalVatRefund().getAmount()));
            saleContractRespVO.setTotalVatRefundCurrency(saleContractRespVO.getTotalVatRefund().getCurrency());
        }

        // 订单毛利拆分
        if (saleContractRespVO.getOrderGrossProfit() != null) {
            saleContractRespVO.setOrderGrossProfitAmount(NumberFormatUtil.formatAmount(saleContractRespVO.getOrderGrossProfit().getAmount()));
            saleContractRespVO.setOrderGrossProfitCurrency(saleContractRespVO.getOrderGrossProfit().getCurrency());
        }

        // 应收汇款拆分
        if (saleContractRespVO.getReceivableExchange() != null) {
            saleContractRespVO.setReceivableExchangeAmount(NumberFormatUtil.formatAmount(saleContractRespVO.getReceivableExchange().getAmount()));
            saleContractRespVO.setReceivableExchangeCurrency(saleContractRespVO.getReceivableExchange().getCurrency());
        }

        // 销售总金额拆分
        if (saleContractRespVO.getTotalAmount() != null) {
            saleContractRespVO.setTotalAmountAmount(NumberFormatUtil.formatAmount(saleContractRespVO.getTotalAmount().getAmount()));
            saleContractRespVO.setTotalAmountCurrency(saleContractRespVO.getTotalAmount().getCurrency());
        }

        // 销售总金额USD拆分
        if (saleContractRespVO.getTotalAmountUsd() != null) {
            saleContractRespVO.setTotalAmountUsdAmount(NumberFormatUtil.formatAmount(saleContractRespVO.getTotalAmountUsd().getAmount()));
            saleContractRespVO.setTotalAmountUsdCurrency(saleContractRespVO.getTotalAmountUsd().getCurrency());
        }
    }

    default List<SaleContractSimpleRespVO> convertSaleContractSimpleRespVO(List<SaleContractDO> saleContractDOList, Map<Long, List<SaleContractItem>> contractItemMap, Map<String, List<AddSubItem>> addsubItemMap, Map<Long, List<CollectionPlan>> collectionPlanMap) {
        return CollectionUtils.convertList(saleContractDOList, saleContractDO -> {
            Long contractId = saleContractDO.getId();
            SaleContractSimpleRespVO saleContractRespVO = convert1(saleContractDO);
            if (CollUtil.isNotEmpty(contractItemMap)) {
                saleContractRespVO.setChildren(contractItemMap.get(contractId));
            }

            if (CollUtil.isNotEmpty(collectionPlanMap)) {
                List<CollectionPlan> collectionPlanList = collectionPlanMap.get(contractId);
                if (CollUtil.isNotEmpty(collectionPlanList)) {
                    // 获取币种
                    Optional<String> currencyOptional = collectionPlanList.stream()
                            .map(CollectionPlan::getReceivableAmount)
                            .filter(Objects::nonNull)
                            .map(s -> {
                                if (StrUtil.isEmpty(s.getCurrency())) {
                                    return CommonDict.EMPTY_STR;
                                }
                                return s.getCurrency();
                            })
                            .findFirst();

                }
            }
            return saleContractRespVO;
        });
    }


    @Mapping(target = "processInstanceId", ignore = true)
    SaleContractChange convertSaleContractChange(SaleContractRespVO saleContractRespVO);


    @Mapping(target = "code",source = "sourceCode")
    @Mapping(target = "auditStatus",ignore = true)
    @Mapping(target = "confirmFlag",ignore = true)
    @Mapping(target = "id",ignore = true)
    SaleContractSaveReqVO convertSaleContractSaveReqVOByChange(SaleContractChange change);

    SaleContractItemExportVO convertSaleContractItemExportVO(SaleContractItem saleContractItem);
    default List<SaleContractItemExportVO> convertSaleContractItemExportVOList(List<SaleContractItem> saleContractItem) {
        return CollectionUtils.convertList(saleContractItem, s -> {
            SaleContractItemExportVO settlementFormItemExportVO =  convertSaleContractItemExportVO(s);
            if(s.getQuantity()!=null && s.getPurchaseWithTaxPrice().getAmount()!=null){
                settlementFormItemExportVO.setPurchaseTotalAmount(NumUtil.mul(s.getQuantity(), s.getPurchaseWithTaxPrice().getAmount()));
            }
            return settlementFormItemExportVO;
        });
    }

    SaleContractAddSubItemExportVO convertSaleContractAddSubItemExportVO(AddSubItem addSubItem);
    default List<SaleContractAddSubItemExportVO> convertSaleContractAddSubItemExportVOList(List<AddSubItem> addSubItem) {
        return CollectionUtils.convertList(addSubItem, this::convertSaleContractAddSubItemExportVO);
    }

    @Mapping(target = "saleContractItemId",source = "id")
    @Mapping(target = "withTaxPrice",source = "realPurchaseWithTaxPrice")
    SavePurchaseContractItemReqVO convertSavePurchaseContractItemReqVO(SaleContractItem saleContractItem);

    default List<SavePurchaseContractItemReqVO> convertSavePurchaseContractItemReqVOList(List<SaleContractItem> saleContractItem) {
        return CollectionUtils.convertList(saleContractItem, this::convertSavePurchaseContractItemReqVO);
    }

    SaleContractItemDTO toItemDTO(SaleContractItem saleContractItem);

    default List<StockChildVO> convertStockChildVOByItems(List<SaleContractItem> saleContractItemList){
        return CollectionUtils.convertList(saleContractItemList,  s -> {
            StockChildVO stockChildVO = convertStockChildVOBySaleContractItem(s);
            return stockChildVO;
        });
    }

    @Mapping(target = "saleContractCode", source = "contractCode")
    @Mapping(target = "skuName", source = "name")
    @Mapping(target = "saleContractItemId", source = "id")
    StockChildVO convertStockChildVOBySaleContractItem(SaleContractItem saleContractItem);

    default List<StockChildVO> convertStockChildVOListBySaleContractItem(Map<String,List<StockLockRespVO>> stockLockRespVOMap, Map<String,List<StockDTO>> stockDTOMap, List<SaleContractItem> saleContractItemList,Map<Long, StockDTO> stockMap, Map<String, Integer> skuTypeMap,SaleContractDO saleContractDO){
        return saleContractItemList.stream().map(s->{
            String skuCode = s.getSkuCode();
            StockChildVO stockChildVO = convertStockChildVOBySaleContractItem(s);
            stockChildVO.setSourceUniqueCode(s.getUniqueCode());
            stockChildVO.setConverNoticeFlag(s.getConverNoticeFlag());
            stockChildVO.setSkuType(skuTypeMap.get(skuCode));
            stockChildVO.setSaleContractItemId(s.getId());
            // 查询产品明细库存
            List<BatchChildVO> batchChildVOList = new ArrayList<>();
            // 库存表中明细
            List<StockDTO> stockDTOS = stockDTOMap.get(s.getContractCode()+skuCode+s.getPurchaseContractCode());
            // 锁定库存明细
            List<StockLockRespVO> stockLockRespVOS = stockLockRespVOMap.get(s.getContractCode()+s.getId());
            // 获取真实锁定库存
            AtomicReference<Integer> realLockQuantity = new AtomicReference<>(0);
            if (CollUtil.isNotEmpty(stockLockRespVOS)){
                stockLockRespVOS.forEach(stockLockRespVO->{
                    realLockQuantity.set(realLockQuantity.get()+stockLockRespVO.getLockQuantity()-stockLockRespVO.getCabinetQuantity());
                });
            }
            // 拆分采购出运不需要查询库存信息
            if (CollUtil.isNotEmpty(stockDTOS)&& BooleanEnum.NO.getValue().equals(s.getSplitPurchaseFlag())){
                stockDTOS = stockDTOS.stream().sorted(Comparator.comparing(StockDTO::getBatchCode)).toList();
                List<BatchChildVO> stockChildList = convertBatchildByStockList(s,stockDTOS,realLockQuantity.get());
                if (CollUtil.isNotEmpty(stockChildList)) {
                    batchChildVOList.addAll(stockChildList.stream().filter(x->x.getAvailableCabinetQuantity()>0).collect(Collectors.toList()));
                }
            }
            // 锁定库存明细
            if (CollUtil.isNotEmpty(stockLockRespVOS)){
                // 仅需添加未在库存中的锁定批次即可，避免重复数据
                List<String> batchCodeList = batchChildVOList.stream().map(BatchChildVO::getBatchCode).toList();
                List<BatchChildVO> stockLockChildList = convertBatchildByStockLockList(s,stockLockRespVOS, stockMap,saleContractDO);
                if (CollUtil.isNotEmpty(stockLockChildList)){
                    batchChildVOList.addAll(stockLockChildList.stream().filter(x->Objects.nonNull(x)&&!batchCodeList.contains(x.getBatchCode())).toList());
                }
            }
            stockChildVO.setChildren(batchChildVOList);
            stockChildVO.setAvailableCabinetQuantity(
                    batchChildVOList.stream().map(BatchChildVO::getAvailableCabinetQuantity).filter(Objects::nonNull).reduce(0, Integer::sum)
            );
            stockChildVO.setProducingQuantity(
                    batchChildVOList.stream().map(BatchChildVO::getProducingQuantity).filter(Objects::nonNull).reduce(0, Integer::sum)
            );
            return stockChildVO;
        }).toList();
    }
    default List<BatchChildVO> convertBatchildByStockList(SaleContractItem SaleContractItem,List<StockDTO> stockDTOList,Integer realLockQuantity){
        return CollectionUtils.convertList(stockDTOList, s->{
            BatchChildVO batchChildVO = convertBatchildByStock(s);
            batchChildVO.setInboundDate(s.getReceiptTime());
            // 可拉柜数量 = 可用数量+在制数量
            batchChildVO.setAvailableCabinetQuantity(s.getAvailableQuantity() + s.getProducingQuantity());
            Integer boxCount = NumberUtil.div(batchChildVO.getAvailableCabinetQuantity(), batchChildVO.getQtyPerOuterbox(), 0, RoundingMode.UP).intValue();
            batchChildVO.setBoxCount(boxCount);
            batchChildVO.setUsedQuantity(Math.min(s.getAvailableQuantity()+s.getProducingQuantity()-s.getCabinetQuantity(),SaleContractItem.getQuantity() - realLockQuantity));
            batchChildVO.setStockMethod(2);
            return batchChildVO;
        });
    }

    BatchChildVO convertBatchildByStock(StockDTO stockDTO);

    @Mapping(target = "availableCabinetQuantity",source = "lockQuantity")
    BatchChildVO convertBatchildByStockLock(StockLockRespVO stockLockRespVO);

    default List<BatchChildVO> convertBatchildByStockLockList(SaleContractItem SaleContractItem, List<StockLockRespVO> stockDTOList, Map<Long, StockDTO> stockMap,SaleContractDO saleContractDO){
        AtomicReference<Integer> realLockQuantity = new AtomicReference<>(SaleContractItem.getRealLockQuantity());
        if (Objects.isNull(realLockQuantity.get())||realLockQuantity.get() == 0){
            return List.of();
        }
        return CollectionUtils.convertList(stockDTOList,s->{
            if (realLockQuantity.get() == 0){
                return null;
            }
            // 过滤已经转过出运的锁定库存
            if (Objects.equals(s.getLockQuantity(), s.getCabinetQuantity())){
                return null;
            }
            BatchChildVO batchChildVO = convertBatchildByStockLock(s);
            if (CollUtil.isEmpty(stockMap)){
                return batchChildVO;
            }
            StockDTO stockDTO = stockMap.get(s.getStockId());
            if (Objects.isNull(stockDTO)){
                return batchChildVO;
            }
            Integer lockQuantity = Objects.isNull(s.getLockQuantity())?0:s.getLockQuantity()-s.getCabinetQuantity();
            if (lockQuantity >= realLockQuantity.get()){
                int usedQuantity = realLockQuantity.get();
                batchChildVO.setUsedQuantity(usedQuantity);
                realLockQuantity.set(0);
            }else {
                batchChildVO.setUsedQuantity(lockQuantity);
                realLockQuantity.set(realLockQuantity.get() - lockQuantity);
            }

            batchChildVO.setPurchaseContractCode(stockDTO.getPurchaseContractCode());
            batchChildVO.setPurchaseContractId(stockDTO.getPurchaseContractId());
            batchChildVO.setVenderId(stockDTO.getVenderId());
            batchChildVO.setVenderCode(stockDTO.getVenderCode());
            batchChildVO.setVenderName(stockDTO.getVenderName());
            batchChildVO.setInboundDate(stockDTO.getReceiptTime());
            batchChildVO.setWarehouseId(stockDTO.getWarehouseId());
            batchChildVO.setWarehouseName(stockDTO.getWarehouseName());
            batchChildVO.setQtyPerInnerbox(SaleContractItem.getQtyPerInnerbox());
            batchChildVO.setQtyPerOuterbox(SaleContractItem.getQtyPerOuterbox());
            batchChildVO.setSpecificationList(stockDTO.getSpecificationList());
            batchChildVO.setStockMethod(1);
            return batchChildVO;
        });
    }

    @Mapping(target = "status", ignore = true)
    PushOytShipmentPlanItem convertShipmentPlanItem(SaleContractItem saleContractItem);

    default List<SaleContractItem> transformPurchaseMsg(List<SaleContractItem> saleContractItems, Map<Long,List<SimplePurchaseContractItemDTO>> purchaseContractItemMap,  Map<String,Map<String, BigDecimal>> dailyRateMap,Map<Long, SimpleShipmentItemDTO> simpleShipmentItemDTOMap) {
        if (CollUtil.isEmpty(saleContractItems)||CollUtil.isEmpty(purchaseContractItemMap)) {
            return List.of();
        }
        saleContractItems.forEach(s->{
            List<SimplePurchaseContractItemDTO> purchaseContractItemDTOList = purchaseContractItemMap.get(s.getId());
            if (CollUtil.isEmpty(purchaseContractItemDTOList)){
                return;
            }
            if (CollUtil.isNotEmpty(simpleShipmentItemDTOMap)){
                SimpleShipmentItemDTO simpleShipmentItemDTO = simpleShipmentItemDTOMap.get(s.getId());
                if (Objects.nonNull(simpleShipmentItemDTO)){
                    s.setRealSplitBoxFlag(simpleShipmentItemDTO.getSplitBoxFlag());
                    s.setRealSpecificationList(simpleShipmentItemDTO.getSpecificationList());
                }
            }
            String skuCode = s.getSkuCode();
            // 处理普通采购 仅一条对应采购明细且产品编号相同
            if (purchaseContractItemDTOList.size()==1){
                purchaseContractItemDTOList.stream().findFirst().ifPresent(purchaseContractItemDTO -> {
                    if (!Objects.equals(purchaseContractItemDTO.getSkuCode(), skuCode)){
                        return;
                    }
                    transformSaleContractItemByPurchase(purchaseContractItemDTO,s);
                });
            }else {
                // 处理拆分采购
                transformSaleContractItemByPurchaseList(purchaseContractItemDTOList,s,dailyRateMap);
            }
        });
        return saleContractItems;
    }

    private void transformSaleContractItemByPurchaseList(List<SimplePurchaseContractItemDTO> simplePurchaseContractItemDTOs,SaleContractItem saleContractItem, Map<String,Map<String, BigDecimal>> dailyRateMap){
        if (CollUtil.isEmpty(simplePurchaseContractItemDTOs)){
            return;
        }
        String purchaseCurrency = saleContractItem.getPurchaseCurrency();
        saleContractItem.setRealPrchaseCurrency(purchaseCurrency);
        Map<String, BigDecimal> rateMap = dailyRateMap.get(saleContractItem.getContractCode());
        if (CollUtil.isEmpty(rateMap)){
            throw exception(CURRENCY_RATE_NOT_EXISTS,saleContractItem.getContractCode());
        }
        BigDecimal rate = rateMap.get(purchaseCurrency);
        if (Objects.isNull(rate)){
            throw exception(CURRENCY_RATE_NOT_EXISTS,purchaseCurrency);
        }
        AtomicReference<BigDecimal> addPackagingPrice = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal> addShippingPrice = new AtomicReference<>(BigDecimal.ZERO);
        simplePurchaseContractItemDTOs.forEach(s->{
            Optional.ofNullable(s.getPackagingPrice()).ifPresent(jsonPackagingPrice->{
                BigDecimal packagingPrice = Optional.ofNullable(jsonPackagingPrice.getAmount()).orElse(BigDecimal.ZERO);
                BigDecimal packingRate = rateMap.get(jsonPackagingPrice.getCurrency());
                if (Objects.isNull(packingRate)||BigDecimal.ZERO.compareTo(packingRate) == 0){
                    addPackagingPrice.set(NumUtil.add(addPackagingPrice.get(),packingRate));
                }else {
                    addPackagingPrice.set(NumUtil.add(addPackagingPrice.get(),NumUtil.mul(NumUtil.div(packagingPrice,packingRate),rate)));
                }

            });
            Optional.ofNullable(s.getShippingPrice()).ifPresent(jsonShippingPrice->{
                BigDecimal shipingPrice = Optional.ofNullable(jsonShippingPrice.getAmount()).orElse(BigDecimal.ZERO);
                BigDecimal shippingRate = rateMap.get(jsonShippingPrice.getCurrency());
                if (Objects.isNull(shippingRate)||BigDecimal.ZERO.compareTo(shippingRate) == 0){
                    addPackagingPrice.set(NumUtil.add(addShippingPrice.get(),shippingRate));
                }else {
                    addPackagingPrice.set(NumUtil.add(addShippingPrice.get(),NumUtil.mul(NumUtil.div(shipingPrice,shippingRate),rate)));
                }

            });
            saleContractItem.setRealTaxRefundRate(s.getTaxRefundRate().compareTo(saleContractItem.getTaxRefundRate())<0?s.getTaxRefundRate():saleContractItem.getTaxRefundRate());
        });
        saleContractItem.setRealPackagingPrice(new JsonAmount().setAmount(addPackagingPrice.get()).setCurrency(purchaseCurrency));
        saleContractItem.setRealShippingPrice(new JsonAmount().setAmount(addShippingPrice.get()).setCurrency(purchaseCurrency));
    }
    private void transformSaleContractItemByPurchase(SimplePurchaseContractItemDTO simplePurchaseContractItemDTO,SaleContractItem saleContractItem){
        saleContractItem.setRealPurchaseQuantity(simplePurchaseContractItemDTO.getQuantity());
        saleContractItem.setRealShippingPrice(simplePurchaseContractItemDTO.getShippingPrice());
        saleContractItem.setRealPackagingPrice(simplePurchaseContractItemDTO.getPackagingPrice());
        saleContractItem.setRealPrchaseCurrency(simplePurchaseContractItemDTO.getPurchaseCurrency());
        if (Objects.isNull(saleContractItem.getRealSplitBoxFlag())){
            saleContractItem.setRealSplitBoxFlag(simplePurchaseContractItemDTO.getSplitBoxFlag());
        }
        if (CollUtil.isEmpty(saleContractItem.getRealSpecificationList())){
            saleContractItem.setRealSpecificationList(simplePurchaseContractItemDTO.getSpecificationList());
        }
        saleContractItem.setRealVenderId(simplePurchaseContractItemDTO.getVenderId());
        saleContractItem.setRealVenderCode(simplePurchaseContractItemDTO.getVenderCode());
        saleContractItem.setRealVenderName(simplePurchaseContractItemDTO.getVenderName());
        saleContractItem.setPurchaseUser(simplePurchaseContractItemDTO.getPurchaseUser());
        saleContractItem.setRealTaxRefundRate(simplePurchaseContractItemDTO.getTaxRefundRate());
    }
}