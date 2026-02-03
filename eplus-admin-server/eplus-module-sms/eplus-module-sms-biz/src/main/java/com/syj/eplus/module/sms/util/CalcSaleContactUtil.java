package com.syj.eplus.module.sms.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.dict.CommonDict;
import cn.iocoder.yudao.framework.mybatis.core.enums.ChangeTypeEnum;
import com.syj.eplus.framework.common.dict.CalculationDict;
import com.syj.eplus.framework.common.dict.SaleContractDict;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.enums.*;
import com.syj.eplus.framework.common.util.CalcSpecificationUtil;
import com.syj.eplus.framework.common.util.NumUtil;
import com.syj.eplus.module.crm.enums.cust.TransportTypeEnum;
import com.syj.eplus.module.sms.controller.admin.salecontract.vo.SaleContractSaveReqVO;
import com.syj.eplus.module.sms.dal.dataobject.addsubitem.AddSubItem;
import com.syj.eplus.module.sms.dal.dataobject.salecontract.SaleContractDO;
import com.syj.eplus.module.sms.dal.dataobject.salecontractitem.SaleContractItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.framework.common.dict.SaleContractDict.*;
import static com.syj.eplus.module.sms.enums.ErrorCodeConstants.*;

/**
 * @Description：销售合同计算工具类
 * @Author：du
 * @Date：2024/6/18 17:17
 */
public class CalcSaleContactUtil {
    protected static final Logger logger = LoggerFactory.getLogger(CalcSaleContactUtil.class);
    /**
     * 40尺高柜阈值
     */
    private static final BigDecimal FORTY_FOOT_CONTAINER_THRESHOLD = new BigDecimal("68000000");
    /**
     * 40尺柜阈值
     */
    private static final BigDecimal FORTY_NINE_CABINET_THRESHOLD = new BigDecimal("59000000");
    /**
     * 20尺柜阈值(上边界)
     */
    private static final BigDecimal FIFTY_NINE_THRESHOLD = new BigDecimal("49000000");
    /**
     * 20尺柜阈值(下边界)
     */
    private static final BigDecimal TWENTY_NINE_THRESHOLD = new BigDecimal("29000000");
    /**
     * 散货
     */
    private static final BigDecimal TWENTY_THRESHOLD = new BigDecimal("20000000");

    /**
     * 计算明细费用
     *
     * @param saleContract 销售合同DO
     */
    public static void checkItemCost(SaleContractSaveReqVO saleContract, Map<String, String> configMap, Map<String, BigDecimal> currencyRateMap) {
        List<SaleContractItem> saleContractItemList = saleContract.getChildren();
        if (CollUtil.isEmpty(saleContractItemList)) {
            return;
        }
        // 删除的明细不参与计算
        saleContractItemList = saleContractItemList.stream().filter(s-> !ChangeTypeEnum.DELETED.getType().equals(s.getChangeFlag())).toList();
        // 将系统配重中value转为可执行计算的decimal
        Map<String, BigDecimal> transformedConfigMap = convertConfigValue(configMap);
        // 表单拖柜费
        JsonAmount trailerFee = saleContract.getTrailerFee();
        // 体积合计
        BigDecimal totalVolume = saleContract.getTotalVolume();
        if (Objects.isNull(totalVolume) || totalVolume.compareTo(BigDecimal.ZERO) == CalculationDict.ZERO) {
            throw exception(CONTRACT_ZERO_VOLUME);
        }
        BigDecimal totalCabinetFee = BigDecimal.ZERO;
        // 表单预估总费用
        if(TransportTypeEnum.OCEAN_SHIPPING.getValue().equals(saleContract.getTransportType())){
            // 计算拖柜数量
            Map<String, BigDecimal> cabinetMap = calcCabinetNum(totalVolume,configMap);
            // 校验前端尺柜数量
            checkCabinet(cabinetMap, saleContract);
            // 20尺柜数量
            Integer twentyFootCabinetNum = saleContract.getTwentyFootCabinetNum();
            // 20尺柜费用
            BigDecimal twentyFootCabinetFee = transformedConfigMap.get(SaleContractDict.TWENTY_FOOT_CABINET_FEE);
            // 40尺柜数量
            Integer fortyFootCabinetNum = saleContract.getFortyFootCabinetNum();
            // 40尺柜费用
            BigDecimal fortyFootCabinetFee = transformedConfigMap.get(SaleContractDict.FORTY_FOOT_CABINET_FEE);
            // 40尺高柜数量
            Integer fortyFootContainerNum = saleContract.getFortyFootContainerNum();
            // 40尺高柜费用
            BigDecimal fortyFootContainerFee = transformedConfigMap.get(SaleContractDict.FORTY_FOOT_CONTAINER_FEE);
            // 散货体积
            BigDecimal bulkHandlingVolume = saleContract.getBulkHandlingVolume();
            // 散货费用
            BigDecimal bulkHandlingFee = transformedConfigMap.get(SaleContractDict.BULK_HANDLING_FEE);
            BigDecimal bulkHandlingStartFee = transformedConfigMap.get(SaleContractDict.BULK_HANDLING_START_FEE);
            // 散货 立方厘米转为立方米
            bulkHandlingVolume = NumUtil.div(bulkHandlingVolume, CalculationDict.ONE_MILLION).setScale(CalculationDict.ZERO, RoundingMode.UP);
            BigDecimal bulkHandlingCost = BigDecimal.valueOf(0);
            if(bulkHandlingVolume.compareTo(BigDecimal.ZERO)>0){
                bulkHandlingCost = bulkHandlingStartFee;
                if(bulkHandlingVolume.compareTo(BigDecimal.ONE)>0){
                    bulkHandlingCost = NumberUtil.add(bulkHandlingCost, NumberUtil.mul(NumUtil.sub(bulkHandlingVolume,BigDecimal.ONE), bulkHandlingFee));
                }
            }
            // 尺柜总费用
             totalCabinetFee = NumberUtil.add(NumberUtil.mul(new BigDecimal(twentyFootCabinetNum), twentyFootCabinetFee),
                    NumberUtil.mul(new BigDecimal(fortyFootCabinetNum), fortyFootCabinetFee),
                    NumberUtil.mul(new BigDecimal(fortyFootContainerNum), fortyFootContainerFee),
                    bulkHandlingCost).setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
        }
        totalCabinetFee = NumberUtil.add(totalCabinetFee, trailerFee.getAmount());
        if (Objects.isNull(saleContract.getEstimatedTotalFreight()) || totalCabinetFee.setScale(CalculationDict.DECIMAL_CHECK, RoundingMode.HALF_UP).compareTo(saleContract.getEstimatedTotalFreight().getCheckAmount()) != CalculationDict.ZERO) {
            throw exception(CONTRACT_TOTAL_TRAILERFEE, totalCabinetFee);
        }
        // 计算明细费用
        checkItemFee(saleContractItemList, totalCabinetFee, totalVolume, trailerFee, currencyRateMap,saleContract.getSaleType());
        // 佣金金额
        checkCommissionAmount(saleContractItemList, saleContract.getCommission());
        // 平台费
        checkPlatformFee(saleContractItemList, currencyRateMap, transformedConfigMap, saleContract.getPlatformFee());
        // 加减项
        checkAddItemFee(saleContract.getAddSubItemList(), saleContract.getAdditionAmount(), saleContract.getDeductionAmount(),currencyRateMap);
        // 毛重合计
        checkTotalGrossWeight(saleContractItemList, saleContract.getTotalGrossweight());
        // 净重合计
        checkTotalWeight(saleContractItemList, saleContract.getTotalWeight());
        // 体积合计
        chackTotalVolume(saleContractItemList, saleContract.getTotalVolume());
        // 货值合计
        checkTotalGoodsValue(saleContractItemList, saleContract.getTotalGoodsValue());
        // 采购合计
        // checkTotalPurchase(saleContractItemList, saleContract.getTotalPurchase(), currencyRateMap);
        // 库存合计
        checkTotalStockCostValue(saleContractItemList, saleContract);
        // 配件采购合计
//        checkAccessoriesPurchaseTotal(saleContractItemList, saleContract.getAccessoriesPurchaseTotal(), currencyRateMap);
        // 退税总额
        checkTotalVatRefund(saleContractItemList, saleContract.getTotalVatRefund());
        // 应收汇款
        if (Objects.isNull(saleContract.getReceivableExchange()) || !NumUtil.bigDecimalsEqual(saleContract.getTotalGoodsValue().getCheckAmount(), saleContract.getReceivableExchange().getCheckAmount())) {
            throw exception(CURRENCY_TOTAL_RECEIVABLE, saleContract.getTotalGoodsValue().getAmount());
        }
        // 销售总金额
        if(Objects.nonNull(saleContract.getTotalAmount()) && Objects.nonNull(saleContract.getTotalAmount().getAmount())){
            checkTotalAmount(saleContractItemList,saleContract,currencyRateMap);
        }
    }


    /**
     * 校验明细金额
     *
     * @param saleContractItemList  销售明细列表
     * @param estimatedTotalFreight 表单预估总费用
     * @param totalVolume           体积合计
     * @param trailerFee            表单拖柜费
     */
    private static void checkItemFee(List<SaleContractItem> saleContractItemList, BigDecimal estimatedTotalFreight, BigDecimal totalVolume, JsonAmount trailerFee, Map<String, BigDecimal> currencyRateMap,Integer saleType) {
        // 表单拖柜费/体积合计
        BigDecimal trailerFeeVolumeDiv;
        if (Objects.nonNull(trailerFee)) {
            // 表单拖柜费
            BigDecimal trailerFeeAmount = trailerFee.getAmount();
            trailerFeeVolumeDiv = NumberUtil.div(trailerFeeAmount, totalVolume).setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
        } else {
            trailerFeeVolumeDiv = BigDecimal.ZERO;
        }
        saleContractItemList.forEach(saleContractItem -> {
            // 校验箱数
            BigDecimal boxCount = checkBoxCount(saleContractItem);
            // 外箱体积
            BigDecimal outerBoxVolume = CalcSpecificationUtil.calcSpecificationTotalVolume(saleContractItem.getSpecificationList());
            // 外箱体积 * 箱数
            BigDecimal outerBoxVolumeTimesQuantity = NumberUtil.mul(outerBoxVolume, boxCount).setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
            // 明细预估总费用 = (表单预估总费用/体积合计)*(外箱体积*箱数)
            BigDecimal itemEstimatedTotalFreight = NumberUtil.div(NumberUtil.mul(estimatedTotalFreight, outerBoxVolumeTimesQuantity),totalVolume).setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
            if (Objects.isNull(saleContractItem.getForecastTotalCost()) || itemEstimatedTotalFreight.setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP).compareTo(saleContractItem.getForecastTotalCost().getCheckAmount()) != CalculationDict.ZERO) {
                throw exception(CONTRACT_ITEM_TOTAL_TRAILERFEE, itemEstimatedTotalFreight);
            }
            // 明细拖柜费 = (表单拖柜费/体积合计)*(外箱体积*箱数)
            BigDecimal itemTrailerFee = NumberUtil.mul(trailerFeeVolumeDiv, outerBoxVolumeTimesQuantity).setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
            if (Objects.isNull(saleContractItem.getTrailerFee()) || itemTrailerFee.setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP).compareTo(saleContractItem.getTrailerFee().getCheckAmount()) != CalculationDict.ZERO) {
                throw exception(CONTRACT_ITEM_TRAILERFEE, itemTrailerFee);
            }
            // 校验明细佣金金额
            checkCommissionFee(saleContractItem);
            if (!SaleTypeEnum.FACTORY_SALE_CONTRACT.getValue().equals(saleType)&&!SaleTypeEnum.DOMESTIC_SALE_CONTRACT.getValue().equals(saleType)){
                //校验明细退税金额
                checkItemVatRefund(saleContractItem.getTaxRefundRate(), saleContractItem.getPurchaseWithTaxPrice(),saleContractItem.getQuantity(), saleContractItem.getTaxRefundPrice(), currencyRateMap);
            }else {
                saleContractItem.setTaxRate(BigDecimal.ZERO);
                saleContractItem.setTaxRefundPrice(new JsonAmount().setAmount(BigDecimal.ZERO).setCurrency(CalculationDict.CURRENCY_RMB));
            }
        });
    }

    /**
     * 转换系统配置中金额项为BigDecimal类型
     *
     * @param configMap 系统配置项map
     * @return 转换结果
     */
    private static Map<String, BigDecimal> convertConfigValue(Map<String, String> configMap) {
        List<String> configKeys = SaleContractDict.getfeeDictList();
        // 先将configKeys中的每一个key初始化为BigDecimal.ZERO 减少后续判空逻辑
        Map<String, BigDecimal> defaultMap = configKeys.stream()
                .collect(Collectors.toMap(
                        key -> key,
                        key -> BigDecimal.ZERO
                ));
        // 合并原始configMap中的数据，如果有值则覆盖默认值
        configMap.forEach((key, value) -> {
            if (configKeys.contains(key) && value != null) {
                defaultMap.put(key, new BigDecimal(value));
            }
        });
        return defaultMap;
    }

    /**
     * 校验佣金金额
     *
     * @param saleContractItem 销售明细
     */
    private static void checkCommissionFee(SaleContractItem saleContractItem) {
        // 外销总金额
        JsonAmount totalSale = saleContractItem.getTotalSaleAmount();
        if (Objects.isNull(totalSale) || CommissionRateEnum.FEE.getType().equals(saleContractItem.getCommissionType())) {
            return;
        }
        BigDecimal totalSaleAmount = totalSale.getAmount();
        // 佣金比例
        BigDecimal commissionRate = saleContractItem.getCommissionRate();
        // 前端传入 佣金金额
        JsonAmount commissionAmount = saleContractItem.getCommissionAmount();
        // 佣金金额
        BigDecimal calcCommissionAmount = NumberUtil.mul(totalSaleAmount, NumUtil.div(commissionRate, new BigDecimal(CommonDict.ONE_HUNDRED)));
        if (Objects.isNull(commissionAmount) || calcCommissionAmount.setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP).compareTo(commissionAmount.getCheckAmount()) != CalculationDict.ZERO) {
            throw exception(CONTRACT_ITEM_ITEM_COMMISSION, calcCommissionAmount);
        }
    }

    /**
     * 计算尺柜/散货数量
     *
     * @param totalVolume 总体积
     * @return 尺柜/散货数量map
     */
    public static Map<String, BigDecimal> calcCabinetNum(BigDecimal totalVolume,Map<String, String> configMap) {
        List<String> baseDictList = SaleContractDict.getbaseDictList();
        Map<String, BigDecimal> cabinetCache = baseDictList.stream()
                .collect(Collectors.toMap(
                        key -> key,
                        key -> BigDecimal.ZERO
                ));
        return getRangeLevel(cabinetCache, totalVolume,configMap);
    }

    /**
     * 计算尺柜/散货数量
     *
     * @param cabinetCache 尺柜/散货数量map
     * @param totalVolume  总体积
     * @return 尺柜/散货数量map
     */

    public static Map<String, BigDecimal> getRangeLevel(Map<String, BigDecimal> cabinetCache, BigDecimal totalVolume,Map<String, String> configMap) {
        if (totalVolume.compareTo(FORTY_FOOT_CONTAINER_THRESHOLD) > 0) {
            totalVolume = computeValue(cabinetCache, totalVolume, FORTY_FOOT_CONTAINER_THRESHOLD, FORTY_FOOT_CONTAINER);
            if (totalVolume.compareTo(BigDecimal.ZERO) == 0) {
                return cabinetCache;
            }
            cabinetCache = getRangeLevel(cabinetCache, totalVolume,configMap);
            // 59< 总体积 <= 68
        } else if (totalVolume.compareTo(FORTY_NINE_CABINET_THRESHOLD) > 0 && totalVolume.compareTo(FORTY_FOOT_CONTAINER_THRESHOLD) < 1) {
            computeValue(cabinetCache, totalVolume, FORTY_NINE_CABINET_THRESHOLD, FORTY_FOOT_CONTAINER);
            // 49< 总体积 <= 59
        } else if (totalVolume.compareTo(FIFTY_NINE_THRESHOLD) > 0 && totalVolume.compareTo(FORTY_NINE_CABINET_THRESHOLD) < 1) {
            computeValue(cabinetCache, totalVolume, FIFTY_NINE_THRESHOLD, FORTY_FOOT_CABINET);
            // 29< 总体积 <= 49
        } else if (totalVolume.compareTo(TWENTY_NINE_THRESHOLD) > 0 && totalVolume.compareTo(FIFTY_NINE_THRESHOLD) < 1) {
            totalVolume = computeValue(cabinetCache, totalVolume, TWENTY_NINE_THRESHOLD, TWENTY_FOOT_CABINET);
            if (totalVolume.compareTo(BigDecimal.ZERO) == 0) {
                return cabinetCache;
            }
            cabinetCache = getRangeLevel(cabinetCache, totalVolume,configMap);
            // 20<= 总体积 <= 29
        } else if (totalVolume.compareTo(TWENTY_THRESHOLD) > -1 && totalVolume.compareTo(TWENTY_NINE_THRESHOLD) < 1) {
            computeValue(cabinetCache, totalVolume, TWENTY_THRESHOLD, TWENTY_FOOT_CABINET);
        } else {
            BigDecimal bulkHandlingFee = new BigDecimal(configMap.get(SaleContractDict.BULK_HANDLING_FEE));
            BigDecimal bulkHandlingStartFee = new BigDecimal(configMap.get(SaleContractDict.BULK_HANDLING_START_FEE));
            BigDecimal twentyFootCabinetFee = new BigDecimal(configMap.get(SaleContractDict.TWENTY_FOOT_CABINET_FEE));
            if(totalVolume.compareTo(BigDecimal.ZERO)>0){
                BigDecimal bulkHandlingVolume = NumUtil.div(totalVolume, CalculationDict.ONE_MILLION).setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
                BigDecimal bulkHandlingCost = NumberUtil.add(bulkHandlingStartFee, NumberUtil.mul(NumUtil.sub(bulkHandlingVolume,CalculationDict.ONE).setScale(CalculationDict.ZERO,RoundingMode.UP), bulkHandlingFee));
                if(bulkHandlingCost.compareTo(twentyFootCabinetFee)>0){
                    BigDecimal twentyNum = cabinetCache.get(SaleContractDict.TWENTY_FOOT_CABINET).add(BigDecimal.ONE);
                    cabinetCache.remove(SaleContractDict.TWENTY_FOOT_CABINET);
                    cabinetCache.put(SaleContractDict.TWENTY_FOOT_CABINET,twentyNum);
                }else{
                    cabinetCache.put(BULK_HANDLING, totalVolume);
                }
            }
        }
        return cabinetCache;
    }

    /**
     * 计算尺柜/散货数量
     *
     * @param cabinetCache 尺柜/散货数量map
     * @param totalVolume  总体积
     * @param threshold    计算阈值
     * @param cabinetKey   尺柜对应缓存key
     * @return 尺柜数量/散货体积
     */

    private static BigDecimal computeValue(Map<String, BigDecimal> cabinetCache, BigDecimal totalVolume, BigDecimal threshold, String cabinetKey) {
        if (totalVolume.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal num = totalVolume.divideToIntegralValue(threshold);
        cabinetCache.compute(cabinetKey, (key, oldValue) -> oldValue != null ? NumberUtil.add(oldValue, num) : num);
        totalVolume = totalVolume.remainder(threshold);
        return totalVolume;
    }

    /**
     * 校验尺柜数量/散货体积
     *
     * @param cabinetMap   尺柜/散货数量map
     * @param saleContract 销售合同
     */

    private static void checkCabinet(Map<String, BigDecimal> cabinetMap, SaleContractSaveReqVO saleContract) {
        if (CollUtil.isEmpty(cabinetMap)) {
            return;
        }
        // 散货体积
        BigDecimal bulkHandlingVolume = cabinetMap.get(BULK_HANDLING);
        if (Objects.isNull(saleContract.getBulkHandlingVolume()) || !NumUtil.bigDecimalsEqual(bulkHandlingVolume.setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP), saleContract.getBulkHandlingVolume().setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP))) {
            throw exception(CONTRACT_BULK_HANDLING_VOLUME, bulkHandlingVolume);
        }
        // 20尺柜
        BigDecimal twentyFootCabinet = cabinetMap.get(TWENTY_FOOT_CABINET);
        if (Objects.isNull(saleContract.getTwentyFootCabinetNum()) || !NumUtil.bigDecimalsEqual(twentyFootCabinet.setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP), NumUtil.parseToBigDecimal(saleContract.getTwentyFootCabinetNum()).setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP))) {
            throw exception(CONTRACT_TWENTY_FOOT_CABINET_NUM, twentyFootCabinet);
        }
        // 40尺柜
        BigDecimal fortyFootCabinet = cabinetMap.get(FORTY_FOOT_CABINET);
        if (Objects.isNull(saleContract.getFortyFootCabinetNum()) || !NumUtil.bigDecimalsEqual(fortyFootCabinet.setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP), NumUtil.parseToBigDecimal(saleContract.getFortyFootCabinetNum()).setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP))) {
            throw exception(CONTRACT_FORTY_FOOT_CABINET_NUM, fortyFootCabinet);
        }
        // 40尺高柜
        BigDecimal fortyFootContainer = cabinetMap.get(FORTY_FOOT_CONTAINER);
        if (Objects.isNull(saleContract.getFortyFootContainerNum()) || !NumUtil.bigDecimalsEqual(fortyFootContainer.setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP), NumUtil.parseToBigDecimal(saleContract.getFortyFootContainerNum()).setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP))) {
            throw exception(CONTRACT_FORTY_FOOT_CONTAINER_NUM, fortyFootContainer);
        }
    }

    /**
     * 校验加减项计算结果
     *
     * @param addSubItemList  加减项列表
     * @param additionAmount  前端传入的加项金额
     * @param deductionAmount 前端传入的减项金额
     */
    private static void checkAddItemFee(List<AddSubItem> addSubItemList, JsonAmount additionAmount, JsonAmount deductionAmount, Map<String, BigDecimal> currencyRateMap) {
        if (CollUtil.isEmpty(addSubItemList)) {
            return;
        }
        List<AddSubItem> addItemList = addSubItemList.stream().filter(s -> CalclationTypeEnum.ADD.getType().equals(s.getCalculationType())).toList();
        List<AddSubItem> subItemList = addSubItemList.stream().filter(s -> CalclationTypeEnum.DEDUCTION.getType().equals(s.getCalculationType())).toList();
        // 前端传入加减项不为空时进行校验
        if (CollUtil.isNotEmpty(addItemList)) {
            BigDecimal additionAmountCalc = addItemList.stream()
                .map(s -> {
                    JsonAmount amount = s.getAmount();
                    if (Objects.isNull(amount)) {
                        return null;
                    }
                    BigDecimal currencyRate = currencyRateMap.get(amount.getCurrency());
                    return NumUtil.mul(amount.getAmount(), currencyRate);
                })
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (Objects.isNull(additionAmount) || !NumUtil.bigDecimalsEqual(additionAmountCalc.setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP), additionAmount.getCheckAmount())) {
                throw exception(CONTRACT_ADD_ITEM_FEE, additionAmountCalc);
            }
        }
        if (CollUtil.isNotEmpty(subItemList)) {
            BigDecimal deductionAmountCalc = subItemList.stream()
                    .map(s -> {
                        JsonAmount amount = s.getAmount();
                        if (Objects.isNull(amount)) {
                            return null;
                        }
                        BigDecimal currencyRate = currencyRateMap.get(amount.getCurrency());
                        return NumUtil.mul(amount.getAmount(), currencyRate);
                    })
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (Objects.isNull(deductionAmount) || !NumUtil.bigDecimalsEqual(deductionAmountCalc.setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP), deductionAmount.getCheckAmount())) {
                throw exception(CONTRACT_SUB_ITEM_FEE, deductionAmountCalc);
            }
        }
    }

    /**
     * 校验毛重合计
     *
     * @param saleContractItemList 销售明细列表
     * @param totalGroussweight    前端传入的毛重合计
     */
    private static void checkTotalGrossWeight(List<SaleContractItem> saleContractItemList, JsonWeight totalGroussweight) {
        if (CollUtil.isEmpty(saleContractItemList)) {
            return;
        }
        BigDecimal totalGroussweightCalc = saleContractItemList.stream()
                .map(s -> {
                    JsonWeight outerboxGrossweight = CalcSpecificationUtil.calcSpecificationTotalGrossweight(s.getSpecificationList());
                    if (Objects.isNull(outerboxGrossweight)) {
                        return null;
                    }
                    String unit = outerboxGrossweight.getUnit();
                    BigDecimal weight = outerboxGrossweight.getWeight();
                    BigDecimal boxCount = new BigDecimal(s.getBoxCount());
                    if (CalculationDict.GRAM.equals(unit)) {
                        weight = NumUtil.div(weight, CalculationDict.ONE_THOUSAND);
                    }
                    return NumUtil.mul(boxCount, weight);
                })
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (Objects.isNull(totalGroussweight) || !NumUtil.bigDecimalsEqual(totalGroussweightCalc.setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP), totalGroussweight.getWeight().setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP))) {
            throw exception(CONTRACT_TOTAL_GROUSSWEIGHT, totalGroussweightCalc);
        }
    }

    /**
     * 校验净重合计
     *
     * @param saleContractItemList 销售明细列表
     * @param totalWeight          前端传入的净重合计
     */
    private static void checkTotalWeight(List<SaleContractItem> saleContractItemList, JsonWeight totalWeight) {
        if (CollUtil.isEmpty(saleContractItemList)) {
            return;
        }
        BigDecimal totalWeightCalc = saleContractItemList.stream()
                .map(s -> {
                    JsonWeight outerboxNetweight = CalcSpecificationUtil.calcSpecificationTotalNetweight(s.getSpecificationList());
                    if (Objects.isNull(outerboxNetweight)) {
                        return null;
                    }
                    String unit = outerboxNetweight.getUnit();
                    BigDecimal weight = outerboxNetweight.getWeight();
                    BigDecimal boxCount = new BigDecimal(s.getBoxCount());
                    if (CalculationDict.GRAM.equals(unit)) {
                        weight = NumUtil.div(weight, CalculationDict.ONE_THOUSAND);
                    }
                    return NumUtil.mul(boxCount, weight);
                })
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (Objects.isNull(totalWeight) || !NumUtil.bigDecimalsEqual(totalWeightCalc.setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP), totalWeight.getWeight().setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP))) {
            throw exception(CONTRACT_TOTAL_WEIGHT, totalWeightCalc);
        }
    }

    /**
     * 校验体积合计
     *
     * @param saleContractItemList 销售明细列表
     * @param totalVolume          前端传入的体积合计
     */
    private static void chackTotalVolume(List<SaleContractItem> saleContractItemList, BigDecimal totalVolume) {
        if (CollUtil.isEmpty(saleContractItemList)) {
            return;
        }
        BigDecimal totalVolumeCalc = saleContractItemList.stream()
                .map(s -> NumUtil.mul(CalcSpecificationUtil.calcSpecificationTotalVolume(s.getSpecificationList()), new BigDecimal(s.getBoxCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(CalculationDict.DECIMAL_PRECISION,RoundingMode.HALF_UP);
        if (Objects.isNull(totalVolume) || !NumUtil.bigDecimalsEqual(totalVolumeCalc.setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP), totalVolume.setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP))) {
            throw exception(CONTRACT_TOTAL_VOLUME, totalVolumeCalc);
        }
    }

    /**
     * 校验货值合计
     *
     * @param saleContractItemList 销售明细列表
     * @param totalGoodsValue      前端传入的货值合计
     */
    private static void checkTotalGoodsValue(List<SaleContractItem> saleContractItemList, JsonAmount totalGoodsValue) {
        if (CollUtil.isEmpty(saleContractItemList)) {
            return;
        }
        BigDecimal totalGoodsValueCalc = saleContractItemList.stream()
                .map(s -> {
                    JsonAmount totalSaleAmount = s.getTotalSaleAmount();
                    if (Objects.isNull(totalSaleAmount)) {
                        return null;
                    }
                    return totalSaleAmount.getAmount();
                })
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (Objects.isNull(totalGoodsValue) || !NumUtil.bigDecimalsEqual(totalGoodsValueCalc.setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP), totalGoodsValue.getAmount().setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP))) {
            throw exception(CONTRACT_TOTAL_GOODS_VALUE, totalGoodsValueCalc);
        }
    }
    /**
     * 校验库存合计
     *
     * @param saleContractItemList 销售明细列表
     * @param saleContractDO      合同
     */
    private static void checkTotalStockCostValue(List<SaleContractItem> saleContractItemList,SaleContractSaveReqVO saleContractDO) {
        if (CollUtil.isEmpty(saleContractItemList)) {
            return;
        }
        BigDecimal totalStockCostCalc = saleContractItemList.stream()
                .map(s -> {
                    JsonAmount totalStockPrice = s.getStockLockTotalPrice();
                    if (Objects.isNull(totalStockPrice)) {
                        return null;
                    }
                    return totalStockPrice.getAmount();
                })
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        saleContractDO.setTotalStockCost(new JsonAmount().setAmount(totalStockCostCalc).setCurrency(CalculationDict.CURRENCY_RMB));
    }

    /**
     * 校验采购合计
     *
     * @param saleContractItemList 销售明细列表
     * @param totalPurchase        前端传入的采购合计
     */
//    private static void checkTotalPurchase(List<SaleContractItem> saleContractItemList, List<JsonAmount> totalPurchase, Map<String, BigDecimal> currencyRateMap) {
//        if (CollUtil.isEmpty(saleContractItemList)) {
//            return;
//        }
//        BigDecimal totalPurchaseCalc = saleContractItemList.stream()
//                .map(s -> {
//                    JsonAmount totalSaleAmount = s.getPurchaseWithTaxPrice();
//                    if (Objects.isNull(totalSaleAmount)) {
//                        return null;
//                    }
//                    BigDecimal amount = totalSaleAmount.getAmount();
//                    Integer quantity = s.getPurchaseQuantity();
//                    return NumUtil.mul(amount, new BigDecimal(quantity));
//                })
//                .filter(Objects::nonNull)
//                .reduce(BigDecimal.ZERO, BigDecimal::add)
//                .setScale(CalculationDict.DECIMAL_CHECK, RoundingMode.HALF_UP);
//        if (Objects.isNull(totalPurchase) || !NumUtil.bigDecimalsEqual(totalPurchaseCalc, totalPurchase.getAmount())) {
//            throw exception(CONTRACT_TOTAL_PURCHASE, totalPurchaseCalc);
//        }
//    }

    /**
     * 校验配件采购合计
     *
     * @param saleContractItemList     销售明细列表
     * @param accessoriesPurchaseTotal 前端传入的配件采购合计
     */
    private static void checkAccessoriesPurchaseTotal(List<SaleContractItem> saleContractItemList, JsonAmount accessoriesPurchaseTotal, Map<String, BigDecimal> currencyRateMap) {
        if (CollUtil.isEmpty(saleContractItemList)) {
            return;
        }
        BigDecimal totalPurchaseCalc = saleContractItemList.stream()
                .filter(s -> SkuTypeEnum.ACCESSORIES.getValue().equals(s.getSkuType()))
                .map(s -> {
                    JsonAmount totalSaleAmount = s.getPurchaseWithTaxPrice();
                    if (Objects.isNull(totalSaleAmount)) {
                        return null;
                    }
                    BigDecimal amount = totalSaleAmount.getAmount();
                    Integer quantity = s.getNeedPurQuantity();
                    return NumUtil.mul(amount, new BigDecimal(quantity));
                })
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (Objects.isNull(accessoriesPurchaseTotal) || !NumUtil.bigDecimalsEqual(totalPurchaseCalc.setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP), accessoriesPurchaseTotal.getAmount().setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP))) {
            throw exception(CONTRACT_TOTAL_PURCHASE, totalPurchaseCalc);
        }
    }

    /**
     * 校验退税总额
     *
     * @param saleContractItemList 销售明细列表
     * @param totalVatRefund       前端传入的退税总额
     */
    private static void checkTotalVatRefund(List<SaleContractItem> saleContractItemList, JsonAmount totalVatRefund) {
        if (CollUtil.isEmpty(saleContractItemList)) {
            return;
        }
        BigDecimal totalVatRefundCalc = saleContractItemList.stream()
                .map(SaleContractItem::getTaxRefundPrice)
                .filter(Objects::nonNull)
                .map(JsonAmount::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (Objects.isNull(totalVatRefund) || !NumUtil.bigDecimalsEqual(totalVatRefundCalc.setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP), totalVatRefund.getAmount().setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP))) {
            throw exception(CONTRACT_TOTAL_VAT_REFUND, totalVatRefundCalc);
        }
    }

    /**
     * 校验明细退税金额
     *
     * @param taxRefundRate        退税率
     * @param purchaseWithTaxPrice 含税单价
     * @param quantity             销售数量
     * @param taxRefundPrice       退税金额
     */
    private static void checkItemVatRefund(BigDecimal taxRefundRate, JsonAmount purchaseWithTaxPrice,Integer quantity, JsonAmount taxRefundPrice, Map<String, BigDecimal> currencyRateMap) {
        if (Objects.isNull(taxRefundRate)) {
            return;
        }
        if (Objects.isNull(purchaseWithTaxPrice) || purchaseWithTaxPrice.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            return;
        }
        BigDecimal currencyRate = currencyRateMap.get(purchaseWithTaxPrice.getCurrency());
        if (currencyRate == null || BigDecimal.ZERO.compareTo(currencyRate) == 0) {
            throw exception(CONTRACT_CURRENCY_RATE_INVALID, purchaseWithTaxPrice.getCurrency());
        }
        // 将退税率转为百分数
        taxRefundRate = NumUtil.mul(taxRefundRate, new BigDecimal(CalculationDict.ONE_HUNDREDTH));
        // 退税金额 = 退税金额=【含税总价*含税总价汇率/(1+退税率*100%)】*退税率*100%
        BigDecimal taxRefundPriceCalc = NumUtil.mul(NumUtil.div(NumUtil.mul(purchaseWithTaxPrice.getAmount(),BigDecimal.valueOf(quantity), currencyRate), NumUtil.add(new BigDecimal(CalculationDict.ONE), taxRefundRate)), taxRefundRate);
        if (Objects.isNull(taxRefundPrice) || !NumUtil.bigDecimalsEqual(taxRefundPrice.getCheckAmount(), taxRefundPriceCalc.setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP))) {
            throw exception(CONTRACT_TOTAL_VAT_REFUND, taxRefundPriceCalc);
        }
    }

    /**
     * 校验销售总金额
     * @param saleContractSaveReqVO 销售明细列表
     * @param currencyRateMap      币种汇率缓存
     */
    private static void checkTotalAmount(List<SaleContractItem> saleContractItemList,SaleContractSaveReqVO saleContractSaveReqVO, Map<String, BigDecimal> currencyRateMap) {
//        BigDecimal subCommissionAmount = saleContractItemList.stream()
//                .filter(s -> BooleanEnum.YES.getValue().equals(s.getCommissionSubTotal()))
//                .map(s -> {
//                    JsonAmount commissionAmount = s.getCommissionAmount();
//                    if (Objects.isNull(commissionAmount)) {
//                        return null;
//                    }
//                    BigDecimal amount = commissionAmount.getAmount();
//                    return NumUtil.mul(amount, saleContractSaveReqVO.getExchangeRate());
//                })
//                .filter(Objects::nonNull)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalAmount = NumberUtil.mul(saleContractSaveReqVO.getTotalGoodsValue().getAmount(), saleContractSaveReqVO.getExchangeRate())
                .add(NumberUtil.mul(saleContractSaveReqVO.getAdditionAmount().getAmount(),currencyRateMap.get(saleContractSaveReqVO.getAdditionAmount().getCurrency())))
                .subtract(NumberUtil.mul(saleContractSaveReqVO.getDeductionAmount().getAmount(),currencyRateMap.get(saleContractSaveReqVO.getDeductionAmount().getCurrency())))
//                .subtract(subCommissionAmount)
                .setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
        BigDecimal totalAmountCalc = totalAmount.setScale(CalculationDict.DECIMAL_CHECK, RoundingMode.HALF_UP);
        if (!NumUtil.bigDecimalsEqual(totalAmountCalc, saleContractSaveReqVO.getTotalAmount().getAmount().setScale(CalculationDict.DECIMAL_CHECK, RoundingMode.HALF_UP))) {
            throw exception(CONTRACT_TOTAL_FEE, totalAmountCalc);
        }
//        BigDecimal totalAmountUsdCalc = NumberUtil.div(totalAmount,currencyRateMap.get("USD")).setScale(CalculationDict.DECIMAL_CHECK, RoundingMode.HALF_UP);
        BigDecimal totalAmountUsdCalc = NumberUtil.div(totalAmount,saleContractSaveReqVO.getUsdRate()).setScale(CalculationDict.DECIMAL_CHECK, RoundingMode.HALF_UP);
        if (!NumUtil.bigDecimalsEqual(totalAmountUsdCalc, saleContractSaveReqVO.getTotalAmountUsd().getAmount().setScale(CalculationDict.DECIMAL_CHECK, RoundingMode.HALF_UP))) {
            throw exception(CONTRACT_TOTALUSD_FEE, totalAmountUsdCalc);
        }
    }

    /**
     * 校验平台费
     *
     * @param saleContractItemList 销售明细列表
     * @param currencyRateMap      币种汇率缓存
     * @param transformedConfigMap 系统配置缓存
     * @param platformFee          前端传入的平台费
     */
    private static void checkPlatformFee(List<SaleContractItem> saleContractItemList, Map<String, BigDecimal> currencyRateMap, Map<String, BigDecimal> transformedConfigMap, JsonAmount platformFee) {
        // 平台费
        BigDecimal platformFeeCalc = saleContractItemList.stream()
                .map(s -> {
                    JsonAmount totalSaleAmount = s.getTotalSaleAmount();
                    if (Objects.isNull(totalSaleAmount)) {
                        return null;
                    }
//                    BigDecimal currencyRate = currencyRateMap.get(totalSaleAmount.getCurrency());
//                    if (Objects.isNull(currencyRate)) {
//                        throw exception(CURRENCY_RATE_NOT_EXISTS, totalSaleAmount.getCurrency());
//                    }
//                    return NumUtil.mul(totalSaleAmount.getAmount(), currencyRate);
                    return totalSaleAmount.getAmount();
                })
                .filter(Objects::nonNull)
                .map(amount -> amount.multiply(transformedConfigMap.get(PLATFORM_FEE_RATE)))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(CalculationDict.DECIMAL_PRECISION,RoundingMode.HALF_UP);
        if (Objects.isNull(platformFee) || !NumUtil.bigDecimalsEqual(platformFeeCalc.setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP), platformFee.getCheckAmount())) {
            throw exception(CONTRACT_PLATFORM_FEE, platformFeeCalc);
        }
    }

    /**
     * 校验佣金
     *
     * @param saleContractItemList 销售明细列表
     * @param commissionAmount     前端传入的佣金
     */
    private static void checkCommissionAmount(List<SaleContractItem> saleContractItemList, JsonAmount commissionAmount) {
        BigDecimal commissionAmountCalc = saleContractItemList.stream()
                .map(SaleContractItem::getCommissionAmount)
                .filter(Objects::nonNull)
                .map(JsonAmount::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (Objects.isNull(commissionAmount) || commissionAmountCalc.setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP).compareTo(commissionAmount.getCheckAmount()) != CalculationDict.ZERO) {
            throw exception(CONTRACT_COMMISSION, commissionAmountCalc);
        }
    }

    /**
     * 校验箱数
     *
     * @param saleContractItem 销售合同明细
     * @return BigDecimal 箱数
     */
    private static BigDecimal checkBoxCount(SaleContractItem saleContractItem) {
        // 数量
        Integer quantity = saleContractItem.getQuantity();
        // 外箱装量
        Integer qtyPerOuterbox = saleContractItem.getQtyPerOuterbox();
        if (Objects.isNull(qtyPerOuterbox) || qtyPerOuterbox == 0) {
            throw exception(CURRENCY_ITEM_OUTERBOX_NUM, saleContractItem.getSkuCode());
        }
        // 箱数 取整
        BigDecimal boxCount = NumUtil.div(quantity, qtyPerOuterbox).setScale(CalculationDict.ZERO, RoundingMode.UP);
        if (Objects.isNull(saleContractItem.getBoxCount()) || !NumUtil.bigDecimalsEqual(boxCount, BigDecimal.valueOf(saleContractItem.getBoxCount()))) {
            throw exception(CURRENCY_ITEM_BOX_COUNT, boxCount);
        }
        return boxCount;
    }


    /**
     * 计算尺柜装量
     */
    private static Long getContainerCapacity(ContainerTypeEnum type, BigDecimal volume){
        if(Objects.equals(volume, BigDecimal.ZERO)){
            return (long)0;
        }
        BigDecimal total = BigDecimal.ZERO;
        if(ContainerTypeEnum.TWENTY == type){
            total = FIFTY_NINE_THRESHOLD;
        }
        if(ContainerTypeEnum.FORTY == type){
            total = FORTY_NINE_CABINET_THRESHOLD;
        }
        if(ContainerTypeEnum.FORTY_HIGH == type){
            total = FORTY_FOOT_CONTAINER_THRESHOLD;
        }
        BigDecimal divide = total.divide(volume);
        return divide.setScale(0,RoundingMode.FLOOR).longValue();

    }
    public static void calcSaleItemCost(SaleContractItem saleContractItem, SaleContractDO saleContractDO, Map<String, BigDecimal> currencyRateMap,Map<String, String> configMap) {
        if (Objects.isNull(saleContractItem)){
            return;
        }
        // 转换系统配置为可计算项
        Map<String, BigDecimal> connfigDecimalMap = convertConfigValue(configMap);
        // 收入
        JsonAmount addAmountJson = new JsonAmount();
        // 销售数量
        Integer quantity = saleContractItem.getQuantity();
        // 采购数量
        Integer purcahseQuantity = Objects.isNull(saleContractItem.getRealPurchaseQuantity())|| saleContractItem.getRealPurchaseQuantity() == 0 ? saleContractItem.getNeedPurQuantity() : saleContractItem.getRealPurchaseQuantity();
        // 销售单价
        JsonAmount unitPrice = saleContractItem.getUnitPrice();
        // 退税金额
        JsonAmount taxRefundPrice = saleContractItem.getTaxRefundPrice();
        // 销售总金额
        JsonAmount totalSaleAmountJson = BeanUtil.copyProperties(unitPrice,JsonAmount.class);
        // 采购单价
        JsonAmount purchaseWithTaxPrice = saleContractItem.getPurchaseWithTaxPrice();
        // 包材单价
        JsonAmount purchasePackagingPrice = saleContractItem.getPurchasePackagingPrice();
        // 表单预估总运费
        JsonAmount estimatedTotalFreightJson = Optional.ofNullable(saleContractDO.getEstimatedTotalFreight()).orElse(new JsonAmount().setAmount(BigDecimal.ZERO).setCurrency(CalculationDict.CURRENCY_RMB));
        // 表单预估总运费(金额)
        BigDecimal estimatedTotalFreight = Optional.ofNullable(estimatedTotalFreightJson.getAmount()).orElse(BigDecimal.ZERO);
        // 表单总体积
        BigDecimal totalVolume = saleContractDO.getTotalVolume();
        // 表单保险费
        JsonAmount insurance = Optional.ofNullable(saleContractDO.getInsuranceFee()).orElse(new JsonAmount().setAmount(BigDecimal.ZERO).setCurrency(CalculationDict.CURRENCY_RMB));
        // 表单货值合计
        JsonAmount totalGoodsValue = Optional.ofNullable(saleContractDO.getTotalGoodsValue()).orElse(new JsonAmount().setAmount(BigDecimal.ZERO).setCurrency(CalculationDict.CURRENCY_RMB));
        // 销售总金额 = 销售数量 * 销售单价
        BigDecimal totalSaleAmount = NumUtil.mul(quantity, unitPrice.getAmount()).setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
        totalSaleAmountJson.setAmount(totalSaleAmount);
        saleContractItem.setTotalSaleAmount(totalSaleAmountJson);
        String totalSaleAmountCurrency = totalSaleAmountJson.getCurrency();
        // 销售总额币种汇率
        BigDecimal totalSaleAmountCurrencyRate = currencyRateMap.get(totalSaleAmountCurrency);
        if (Objects.isNull(totalSaleAmountCurrencyRate)) {
            throw exception(CURRENCY_RATE_NOT_EXISTS, totalSaleAmountCurrencyRate);
        }
        // 退税率
        if (SaleTypeEnum.DOMESTIC_SALE_CONTRACT.getValue().equals(saleContractDO.getSaleType())){
            saleContractItem.setTaxRefundPrice(new JsonAmount().setAmount(BigDecimal.ZERO).setCurrency(CalculationDict.CURRENCY_RMB));
            saleContractItem.setTaxRefundRate(BigDecimal.ZERO);
        }else {
            BigDecimal realTaxRefundRate = saleContractItem.getRealTaxRefundRate();
            Optional.ofNullable(realTaxRefundRate).ifPresent(s->{
                if (s.compareTo(BigDecimal.ZERO) == 0){
                    saleContractItem.setTaxRefundPrice(new JsonAmount().setAmount(BigDecimal.ZERO).setCurrency(CalculationDict.CURRENCY_RMB));
                    return;
                }
                // 真实采购价
                JsonAmount realPurchaseWithTaxPriceJson = saleContractItem.getRealPurchaseWithTaxPrice();
                // 退税金额
                Optional.ofNullable(realPurchaseWithTaxPriceJson).ifPresent(x->{
                    String currency = realPurchaseWithTaxPriceJson.getCurrency();
                    BigDecimal realPurchaseWithTaxPrice = realPurchaseWithTaxPriceJson.getAmount();
                    if (Objects.nonNull(realPurchaseWithTaxPrice)&& StrUtil.isNotEmpty(currency)){
                        BigDecimal realTaxRefundPrice = NumUtil.mul(NumUtil.div(NumUtil.mul(realPurchaseWithTaxPrice, currencyRateMap.get(currency), quantity), NumUtil.add(NumUtil.mul(realTaxRefundRate, 0.01), 1)), NumUtil.mul(realTaxRefundRate, 0.01));
                        saleContractItem.setTaxRefundPrice(new JsonAmount().setAmount(realTaxRefundPrice).setCurrency(CalculationDict.CURRENCY_RMB));
                    }
                });
            });
        }
        // 箱数
        Integer qtyPerOuterbox = saleContractItem.getQtyPerOuterbox() == 0 ? 1 : saleContractItem.getQtyPerOuterbox();
        Optional.ofNullable(qtyPerOuterbox).ifPresent(s->{
            // 箱数 取整
            BigDecimal boxCount = NumUtil.div(quantity, qtyPerOuterbox).setScale(CalculationDict.ZERO, RoundingMode.UP);
            saleContractItem.setBoxCount(boxCount.intValue());
        });
        // 收入(人民币)
        AtomicReference<BigDecimal> addAmountRmb = new AtomicReference<>(NumUtil.mul(totalSaleAmount, totalSaleAmountCurrencyRate));
        Optional.ofNullable(saleContractItem.getTaxRefundPrice()).ifPresent(s->{
            addAmountRmb.set(NumUtil.add(addAmountRmb.get(),s.getAmount()));
        });
        // 成本(人民币)
        AtomicReference<BigDecimal> subAmountRmb = new AtomicReference<>(BigDecimal.ZERO);
        // 采购成本 --  采购单价*(采购数量-采购赠品数量)
        Optional.ofNullable(purchaseWithTaxPrice).ifPresent(s->{
            // 采购币种
            String currency = Objects.isNull(s.getCurrency())?CalculationDict.CURRENCY_RMB:s.getCurrency();
            // 采购币种汇率
            BigDecimal purchaseRate = currencyRateMap.get(currency);
            Optional.ofNullable(purchaseRate).ifPresent(rate->{
                // 采购总金额
                BigDecimal purchaseTotalAmount = NumUtil.mul(NumUtil.mul(s.getAmount(), rate), purcahseQuantity);
                subAmountRmb.set(NumUtil.add(subAmountRmb.get() , purchaseTotalAmount));
            });
        });
        // 佣金
        Optional.ofNullable(saleContractItem.getCommissionAmount()).ifPresent(s->{
            subAmountRmb.set(NumUtil.add(subAmountRmb.get(),s.getAmount()));
        });
        //包材采购成本  -- 采购单价 * 销售数量
        Optional.ofNullable(purchasePackagingPrice).ifPresent(s->{
            // 包材采购币种
            String currency = Objects.isNull(s.getCurrency())?CalculationDict.CURRENCY_RMB:s.getCurrency();
            // 采购币种汇率
            BigDecimal purchaseRate = currencyRateMap.get(currency);
            Optional.ofNullable(purchaseRate).ifPresent(rate->{
                // 采购总金额
                BigDecimal packingPurchaseTotalAmount = NumUtil.mul(NumUtil.mul(s.getAmount(), rate), quantity);
                subAmountRmb.set(NumUtil.add(subAmountRmb.get(), packingPurchaseTotalAmount));
            });
        });
        // 预估总费用 -- (表单预估总运费 * 外箱体积 * 箱数) / 总体积
        JsonAmount forecastTotalCostJson = BeanUtil.copyProperties(estimatedTotalFreightJson,JsonAmount.class);
        BigDecimal forecastTotalCost;
        if (BigDecimal.ZERO.compareTo(totalVolume) == 0 || BigDecimal.ZERO.compareTo(estimatedTotalFreight) == 0){
            forecastTotalCost = BigDecimal.ZERO;
        }else {
            forecastTotalCost = NumUtil.div(NumUtil.mul(estimatedTotalFreight, saleContractItem.getBoxCount(), CalcSpecificationUtil.calcSpecificationTotalVolume(saleContractItem.getSpecificationList())),totalVolume);
        }
        forecastTotalCostJson.setAmount(forecastTotalCost);
        subAmountRmb.set(NumUtil.add(subAmountRmb.get(), forecastTotalCost));
        saleContractItem.setForecastTotalCost(forecastTotalCostJson);
        // 验货费
        JsonAmount inspectionFee = saleContractItem.getInspectionFee();
        Optional.ofNullable(inspectionFee).flatMap(s -> Optional.ofNullable(s.getAmount())).ifPresent(x -> {
            subAmountRmb.set(NumUtil.add(subAmountRmb.get(), x));
        });
        // 平台费配置比例
        BigDecimal platformFeeRate = connfigDecimalMap.get(PLATFORM_FEE_RATE);
        // 平台费 -- 销售总额 * 平台费配置比例
        JsonAmount platformFeeJson = BeanUtil.copyProperties(unitPrice,JsonAmount.class);
        BigDecimal platformFee = NumUtil.mul(platformFeeRate, totalSaleAmount).setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
        Optional.of(platformFee).ifPresent(s->{
            platformFeeJson.setAmount(platformFee);
            saleContractItem.setPlatformFee(platformFeeJson);
            BigDecimal platformFeeCurrencyRate = currencyRateMap.get(platformFeeJson.getCurrency());
            Optional.ofNullable(platformFeeCurrencyRate).ifPresent(x->{
                BigDecimal platformFeeRmb = NumUtil.mul(platformFee, platformFeeCurrencyRate);
                subAmountRmb.set(NumUtil.add(subAmountRmb.get(), platformFeeRmb));
            });
        });
        if (BigDecimal.ZERO.compareTo(totalSaleAmount) == 0){
            return;
        }
        // 保险费 -- （表单保险费/表单货值合计*明细总额）
        JsonAmount insuranceFeeJson = BeanUtil.copyProperties(insurance,JsonAmount.class);
        BigDecimal insuranceFee = BigDecimal.ZERO;
        if(totalGoodsValue.getAmount() != null && totalGoodsValue.getAmount().compareTo(BigDecimal.ZERO) > 0){
            insuranceFee = NumUtil.mul(NumUtil.div(insurance.getAmount(), totalGoodsValue.getAmount()), totalSaleAmount).setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
        }
        insuranceFeeJson.setAmount(insuranceFee);
        Optional.of(insuranceFee).ifPresent(s->{
            subAmountRmb.set(NumUtil.add(subAmountRmb.get(), s));
            saleContractItem.setInsuranceFee(insuranceFeeJson);
        });
        // 中信保费用配置
        BigDecimal creditInsuranceRate = connfigDecimalMap.get(CREDIT_INSURANCE_RATE);
        // 中信保费用
        Optional.ofNullable(saleContractItem.getSinosureFee()).ifPresent(s->{
            JsonAmount sinosureFeeJson = BeanUtil.copyProperties(s, JsonAmount.class);
            Optional.ofNullable(sinosureFeeJson.getAmount()).ifPresent(x->{
                if (x.compareTo(BigDecimal.ZERO) <= 0){
                    return;
                }
                BigDecimal sinosureFee = NumUtil.div(NumUtil.mul(NumUtil.mul(creditInsuranceRate, totalSaleAmount), totalSaleAmountCurrencyRate), currencyRateMap.get(sinosureFeeJson.getCurrency()));
                sinosureFeeJson.setAmount(sinosureFee);
                saleContractItem.setSinosureFee(sinosureFeeJson);
                subAmountRmb.set(NumUtil.add(subAmountRmb.get(), NumUtil.mul(sinosureFee,currencyRateMap.get(sinosureFeeJson.getCurrency()))));
            });
        });
        //佣金否扣减总金额为是，需要将佣金计算到成本
        Optional.ofNullable(saleContractItem.getCommissionAmount()).ifPresent(s->{
                subAmountRmb.set(NumUtil.add(subAmountRmb.get(), NumUtil.mul(s.getAmount(),currencyRateMap.get(s.getCurrency()))));
            });
        // 利润 = 收入 - 成本
        JsonAmount orderGrossProfitJson = BeanUtil.copyProperties(saleContractItem.getOrderGrossProfit(), JsonAmount.class);
        BigDecimal orderGrossProfitSum = NumUtil.sub(addAmountRmb.get(),subAmountRmb.get());
        orderGrossProfitJson.setAmount(orderGrossProfitSum.setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP));
        saleContractItem.setOrderGrossProfit(orderGrossProfitJson);
        // 利润率 = 利润 / 收入
        saleContractItem.setOrderGrossProfitRate(NumUtil.div(orderGrossProfitJson.getAmount(),addAmountRmb.get().setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP)).setScale(CalculationDict.DECIMAL_PRECISION,RoundingMode.HALF_UP));
    }

    public static void calcSaleContractCost(SaleContractDO saleContractDO,List<SaleContractItem> saleContractItemList, Map<String, BigDecimal> dailyRateMap,List<AddSubItem> addSubItemList){
        if (CollUtil.isEmpty(saleContractItemList)){
            return;
        }
        // 库存合计
        Optional<JsonAmount> stockPriceOpt = saleContractItemList.stream().filter(s -> Objects.nonNull(s.getStockLockPrice()) && s.getStockLockPrice().getAmount().compareTo(BigDecimal.ZERO) > 0)
                .map(s -> {
                    if (Objects.isNull(s.getRealLockQuantity()) || s.getRealLockQuantity() == 0) {
                        return new JsonAmount(BigDecimal.ZERO, CalculationDict.CURRENCY_RMB);
                    }
                    return new JsonAmount(NumUtil.mul(s.getStockLockPrice().getAmount(), s.getRealLockQuantity()), s.getStockLockPrice().getCurrency());
                }).reduce(JsonAmount::add);
        stockPriceOpt.ifPresent(saleContractDO::setTotalStockCost);
        // 退税合计
        BigDecimal totalTaxPrice = saleContractItemList.stream().map(SaleContractItem::getTaxRefundPrice).filter(Objects::nonNull).map(JsonAmount::getAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        String taxCurrency = saleContractItemList.stream().map(SaleContractItem::getTaxRefundPrice).filter(Objects::nonNull).map(JsonAmount::getCurrency).filter(Objects::nonNull) .findFirst().orElse("RMB");
        saleContractDO.setTotalVatRefund(new JsonAmount().setAmount(totalTaxPrice).setCurrency(taxCurrency));
        // 采购合计
        Map<String,BigDecimal> totalPurchasePriceMap = new HashMap<>();
        saleContractItemList.forEach(s->{
            JsonAmount realPurchaseWithTaxPrice = s.getRealPurchaseWithTaxPrice();
            if (Objects.isNull(realPurchaseWithTaxPrice)|| Objects.isNull(realPurchaseWithTaxPrice.getAmount())||StrUtil.isEmpty(realPurchaseWithTaxPrice.getCurrency())){
                return;
            }
            int purchaseQuantity = s.getRealPurchaseQuantity();
            if(purchaseQuantity == 0){
                purchaseQuantity = s.getNeedPurQuantity();
            }
            totalPurchasePriceMap.merge(realPurchaseWithTaxPrice.getCurrency(),NumUtil.mul(realPurchaseWithTaxPrice.getAmount(),purchaseQuantity), BigDecimal::add);
        });
        if (CollUtil.isNotEmpty(totalPurchasePriceMap)){
            List<JsonAmount> totalPurchasePriceList = totalPurchasePriceMap.entrySet().stream().map(s->new JsonAmount().setAmount(s.getValue()).setCurrency(s.getKey())).toList();
            saleContractDO.setTotalPurchase(totalPurchasePriceList);
        }
        // 订单毛利,毛利率
        calcSaleContractOrderGrossProfit(saleContractDO,dailyRateMap,saleContractItemList,addSubItemList);
    }

    /**
     * 计算订单毛利，毛利率
     * @param saleContractDO 销售合同
     * @param currencyRateMap 币种汇率
     * @param saleContractItemList 销售明细
     * @param addSubItemList 加减项
     */
    public static void calcSaleContractOrderGrossProfit(SaleContractDO saleContractDO,Map<String, BigDecimal> currencyRateMap,List<SaleContractItem> saleContractItemList,List<AddSubItem> addSubItemList){
        // 防止 addSubItemList 为 null 导致 NullPointerException
        if (CollUtil.isEmpty(addSubItemList)) {
            addSubItemList = new ArrayList<>();
        }
        List<AddSubItem> addItemList = addSubItemList.stream().filter(s -> CalclationTypeEnum.ADD.getType().equals(s.getCalculationType())).toList();
        List<AddSubItem> subItemList = addSubItemList.stream().filter(s -> CalclationTypeEnum.DEDUCTION.getType().equals(s.getCalculationType())).toList();
        JsonAmount additionAmount = new JsonAmount();
        additionAmount.setCurrency("RMB");
        JsonAmount deductionAmount = new JsonAmount();
        deductionAmount.setCurrency("RMB");
        if (CollUtil.isNotEmpty(addItemList)) {
            BigDecimal additionAmountCalc = addItemList.stream()
                    .map(s -> {
                        JsonAmount amount = s.getAmount();
                        if (Objects.isNull(amount)) {
                            return null;
                        }
                        BigDecimal currencyRate = currencyRateMap.get(amount.getCurrency());
                        return NumUtil.mul(amount.getAmount(), currencyRate);
                    })
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
            additionAmount.setAmount(additionAmountCalc);
        }
        if (CollUtil.isNotEmpty(subItemList)) {
            BigDecimal deductionAmountCalc = subItemList.stream()
                    .map(s -> {
                        JsonAmount amount = s.getAmount();
                        if (Objects.isNull(amount)) {
                            return null;
                        }
                        BigDecimal currencyRate = currencyRateMap.get(amount.getCurrency());
                        return NumUtil.mul(amount.getAmount(), currencyRate);
                    })
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
            deductionAmount.setAmount(deductionAmountCalc);
        }
            if (Objects.nonNull(additionAmount.getAmount())) {
                saleContractDO.setAdditionAmount(additionAmount);
            }
            if (Objects.nonNull(deductionAmount.getAmount())) {
                saleContractDO.setDeductionAmount(deductionAmount);
            }
            //销售总金额重新计算
            BigDecimal subCommissionAmount = saleContractItemList.stream()
                    .filter(s -> BooleanEnum.YES.getValue().equals(s.getCommissionSubTotal()))
                    .map(s -> {
                        JsonAmount commissionAmount = s.getCommissionAmount();
                        if (Objects.isNull(commissionAmount)) {
                            return null;
                        }
                        BigDecimal amount = commissionAmount.getAmount();
                        return NumUtil.mul(amount, currencyRateMap.get(s.getCommissionAmount().getCurrency()));
                    })
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
            BigDecimal totalAmountCalc = NumberUtil.mul(saleContractDO.getTotalGoodsValue().getAmount(), currencyRateMap.get(saleContractDO.getTotalGoodsValue().getCurrency()))
                    .add(NumberUtil.mul(saleContractDO.getAdditionAmount().getAmount(), currencyRateMap.get(saleContractDO.getAdditionAmount().getCurrency())))
                    .subtract(NumberUtil.mul(saleContractDO.getDeductionAmount().getCheckAmount(), currencyRateMap.get(saleContractDO.getDeductionAmount().getCurrency())))
                    .subtract(subCommissionAmount)
                    .setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
            JsonAmount totalAmount = new JsonAmount();
            totalAmount.setCurrency("RMB");
            totalAmount.setAmount(totalAmountCalc);
            saleContractDO.setTotalAmount(totalAmount);
            BigDecimal totalAmountUsdCalc = NumberUtil.div(totalAmountCalc, currencyRateMap.get("USD")).setScale(CalculationDict.DECIMAL_CHECK, RoundingMode.HALF_UP);
            JsonAmount totalAmountUsd = new JsonAmount();
            totalAmountUsd.setCurrency("USD");
            totalAmountUsd.setAmount(totalAmountUsdCalc);
            saleContractDO.setTotalAmountUsd(totalAmountUsd);
        // 收入
        BigDecimal addAmount = NumberUtil.mul(saleContractDO.getTotalGoodsValue().getAmount(), currencyRateMap.get(saleContractDO.getTotalGoodsValue().getCurrency()))
                .add(NumberUtil.mul(saleContractDO.getTotalVatRefund().getAmount(), currencyRateMap.get(saleContractDO.getTotalVatRefund().getCurrency())))
                .add(NumberUtil.mul(saleContractDO.getAdditionAmount().getAmount(), currencyRateMap.get(saleContractDO.getAdditionAmount().getCurrency())))
                .setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
        BigDecimal totalPurchaseCalc = saleContractDO.getTotalPurchase().stream()
                .map(s -> {
                    BigDecimal amount = s.getAmount();
                    return NumUtil.mul(amount, currencyRateMap.get(s.getCurrency()));
                })
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
        BigDecimal subAmount = totalPurchaseCalc
                .add(NumberUtil.mul(saleContractDO.getEstimatedPackingMaterials().getAmount(), currencyRateMap.get(saleContractDO.getEstimatedPackingMaterials().getCurrency())))
//                .add(NumberUtil.mul(saleContractDO.getAccessoriesPurchaseTotal().getAmount(), currencyRateMap.get(saleContractDO.getAccessoriesPurchaseTotal().getCurrency())))
                .add(NumberUtil.mul(saleContractDO.getEstimatedTotalFreight().getAmount(), currencyRateMap.get(saleContractDO.getEstimatedTotalFreight().getCurrency())))
                .add(NumberUtil.mul(saleContractDO.getInspectionFee().getAmount(), currencyRateMap.get(saleContractDO.getInspectionFee().getCurrency())))
                .add(NumberUtil.mul(saleContractDO.getPlatformFee().getAmount(), currencyRateMap.get(saleContractDO.getPlatformFee().getCurrency())))
                .add(NumberUtil.mul(saleContractDO.getDeductionAmount().getAmount(), currencyRateMap.get(saleContractDO.getDeductionAmount().getCurrency())))
                .add(NumberUtil.mul(saleContractDO.getInsuranceFee().getAmount(), currencyRateMap.get(saleContractDO.getInsuranceFee().getCurrency())))
                .add(NumberUtil.mul(saleContractDO.getSinosureFee().getAmount(), currencyRateMap.get(saleContractDO.getSinosureFee().getCurrency())))
                .add(NumberUtil.mul(saleContractDO.getTotalStockCost().getAmount(), currencyRateMap.get(saleContractDO.getTotalStockCost().getCurrency())))
                .add(NumberUtil.mul(saleContractDO.getCommission().getAmount(), currencyRateMap.get(saleContractDO.getCommission().getCurrency())))
                .add(subCommissionAmount)
                .setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
        //毛利润 = 收入 - 成本
        BigDecimal orderGrossProfitCalc = addAmount.subtract(subAmount);
        JsonAmount orderGrossProfit = new JsonAmount();
        orderGrossProfit.setAmount(orderGrossProfitCalc);
        orderGrossProfit.setCurrency("RMB");
        saleContractDO.setOrderGrossProfit(orderGrossProfit);
        //毛利率=毛利润/收入
        BigDecimal grossProfitMargin = BigDecimal.ZERO.compareTo(addAmount) == 0 ?BigDecimal.ZERO:NumberUtil.div(orderGrossProfitCalc, addAmount);
        saleContractDO.setRealGrossProfitMargin(grossProfitMargin);
    }
}
