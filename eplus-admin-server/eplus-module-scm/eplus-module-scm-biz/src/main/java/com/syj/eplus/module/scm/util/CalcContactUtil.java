package com.syj.eplus.module.scm.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.iocoder.yudao.framework.common.dict.CommonDict;
import com.syj.eplus.framework.common.dict.CalculationDict;
import com.syj.eplus.framework.common.dict.SaleContractDict;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.enums.CommissionRateEnum;
import com.syj.eplus.framework.common.enums.ContainerTypeEnum;
import com.syj.eplus.framework.common.enums.SkuTypeEnum;
import com.syj.eplus.framework.common.util.CalcSpecificationUtil;
import com.syj.eplus.framework.common.util.NumUtil;
import com.syj.eplus.module.crm.enums.cust.TransportTypeEnum;
import com.syj.eplus.module.sms.api.dto.SaleContractItemSaveDTO;
import com.syj.eplus.module.sms.api.dto.SaleContractSaveDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.framework.common.dict.SaleContractDict.*;
import static com.syj.eplus.module.sms.enums.ErrorCodeConstants.*;

public class CalcContactUtil {
    protected static final Logger logger = LoggerFactory.getLogger(CalcContactUtil.class);
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
    public static void checkItemCost(SaleContractSaveDTO saleContract, Map<String, String> configMap, Map<String, BigDecimal> currencyRateMap,String currency) {
        List<SaleContractItemSaveDTO> saleContractItemList = saleContract.getChildren();
        if (CollUtil.isEmpty(saleContractItemList)) {
            return;
        }
        // 体积合计
        BigDecimal totalVolumeCalc = chackTotalVolume(saleContractItemList, saleContract.getTotalVolume());
        saleContract.setTotalVolume(totalVolumeCalc);
        // 将系统配重中value转为可执行计算的decimal
        Map<String, BigDecimal> transformedConfigMap = convertConfigValue(configMap);
        // 表单拖柜费
        JsonAmount trailerFee = Objects.isNull(saleContract.getTrailerFee())?new JsonAmount().setAmount(BigDecimal.ZERO):saleContract.getTrailerFee();
        // 体积合计
        BigDecimal totalVolume = saleContract.getTotalVolume();
        if (Objects.isNull(totalVolume) || totalVolume.compareTo(BigDecimal.ZERO) == CalculationDict.ZERO) {
            throw exception(CONTRACT_ZERO_VOLUME);
        }
        // 表单预估总费用
        BigDecimal totalCabinetFee = BigDecimal.ZERO;
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
            bulkHandlingVolume = NumUtil.div(bulkHandlingVolume, CalculationDict.ONE_MILLION).setScale(CalculationDict.ZERO, RoundingMode.HALF_UP);
            BigDecimal bulkHandlingCost = BigDecimal.valueOf(0);
            if(bulkHandlingVolume.compareTo(BigDecimal.ZERO)>0){
                bulkHandlingCost = bulkHandlingStartFee;
                if(bulkHandlingVolume.compareTo(BigDecimal.ONE)>0){
                    bulkHandlingCost = NumberUtil.add(bulkHandlingCost, NumberUtil.mul(NumUtil.sub(bulkHandlingVolume, BigDecimal.ONE), bulkHandlingFee));
                }
            }
            // 尺柜总费用
             totalCabinetFee = NumberUtil.add(NumberUtil.mul(new BigDecimal(twentyFootCabinetNum), twentyFootCabinetFee),
                    NumberUtil.mul(new BigDecimal(fortyFootCabinetNum), fortyFootCabinetFee),
                    NumberUtil.mul(new BigDecimal(fortyFootContainerNum), fortyFootContainerFee),
                    bulkHandlingCost).setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
        }
        totalCabinetFee = NumberUtil.add(totalCabinetFee, trailerFee.getAmount());
        saleContract.setEstimatedTotalFreight(new JsonAmount().setAmount(totalCabinetFee).setCurrency(currency));
        // 计算明细费用
        checkItemFee(saleContractItemList, totalCabinetFee, totalVolume, trailerFee, currencyRateMap,currency);
        // 佣金金额
        JsonAmount commissionAmount = checkCommissionAmount(saleContractItemList, saleContract.getCommission(),currency);
        saleContract.setCommission(commissionAmount);
        // 平台费
        JsonAmount platformFee = checkPlatformFee(saleContractItemList, currencyRateMap, transformedConfigMap, saleContract.getPlatformFee(),currency);
        saleContract.setPlatformFee(platformFee);
        // 毛重合计
        JsonWeight grossWeight = checkTotalGrossWeight(saleContractItemList, saleContract.getTotalGrossweight());
        saleContract.setTotalGrossweight(grossWeight);
        // 净重合计
        JsonWeight totalWeight = checkTotalWeight(saleContractItemList, saleContract.getTotalWeight());
        saleContract.setTotalWeight(totalWeight);
        // 货值合计
        JsonAmount totalGoodsValue = checkTotalGoodsValue(saleContractItemList, saleContract.getTotalGoodsValue(),currency);
        saleContract.setTotalGoodsValue(totalGoodsValue);
        // 采购合计
        // checkTotalPurchase(saleContractItemList, saleContract.getTotalPurchase(), currencyRateMap);
        // 配件采购合计
        JsonAmount accessoriesPurchaseTotal = checkAccessoriesPurchaseTotal(saleContractItemList, saleContract.getAccessoriesPurchaseTotal(), currencyRateMap,currency);
        saleContract.setAccessoriesPurchaseTotal(accessoriesPurchaseTotal);
        // 退税总额
        JsonAmount totalVatRefund = checkTotalVatRefund(saleContractItemList, saleContract.getTotalVatRefund(),currency);
        saleContract.setTotalVatRefund(totalVatRefund);
        // 应收汇款
        saleContract.setReceivableExchange(saleContract.getTotalGoodsValue());
    }

    /**
     * 校验明细金额
     *
     * @param saleContractItemList  销售明细列表
     * @param estimatedTotalFreight 表单预估总费用
     * @param totalVolume           体积合计
     * @param trailerFee            表单拖柜费
     */
    private static void checkItemFee(List<SaleContractItemSaveDTO> saleContractItemList, BigDecimal estimatedTotalFreight, BigDecimal totalVolume, JsonAmount trailerFee, Map<String, BigDecimal> currencyRateMap,String currency) {
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
            if (BigDecimal.ZERO.compareTo(boxCount) == 0){
                return;
            }
            // 外箱体积
            BigDecimal outerBoxVolume = CalcSpecificationUtil.calcSpecificationTotalVolume(saleContractItem.getSpecificationList());
            // 外箱体积 * 箱数
            BigDecimal outerBoxVolumeTimesQuantity = NumberUtil.mul(outerBoxVolume, boxCount).setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
            // 明细预估总费用 = (表单预估总费用/体积合计)*(外箱体积*箱数)
            BigDecimal itemEstimatedTotalFreight = NumberUtil.div(NumberUtil.mul(estimatedTotalFreight, outerBoxVolumeTimesQuantity),totalVolume).setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
            saleContractItem.setForecastTotalCost(new JsonAmount().setAmount(itemEstimatedTotalFreight).setCurrency(currency));
            // 明细拖柜费 = (表单拖柜费/体积合计)*(外箱体积*箱数)
            BigDecimal itemTrailerFee = NumberUtil.mul(trailerFeeVolumeDiv, outerBoxVolumeTimesQuantity).setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
            saleContractItem.setTrailerFee(new JsonAmount().setAmount(itemTrailerFee).setCurrency(currency));
            // 校验明细佣金金额
            checkCommissionFee(saleContractItem,currency);
            //校验明细退税金额
            JsonAmount taxRefundPrice = checkItemVatRefund(saleContractItem.getTaxRefundRate(), saleContractItem.getPurchaseWithTaxPrice(), saleContractItem.getQuantity(), saleContractItem.getTaxRefundPrice(), currencyRateMap,currency);
            saleContractItem.setTaxRefundPrice(taxRefundPrice);
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
    private static void checkCommissionFee(SaleContractItemSaveDTO saleContractItem,String currency) {
        // 外销总金额
        JsonAmount totalSale = saleContractItem.getTotalSaleAmount();
        if (Objects.isNull(totalSale) || CommissionRateEnum.FEE.getType().equals(saleContractItem.getCommissionType())) {
            return;
        }
        BigDecimal totalSaleAmount = totalSale.getAmount();
        // 佣金比例
        BigDecimal commissionRate = saleContractItem.getCommissionRate();
        // 佣金金额
        BigDecimal calcCommissionAmount = NumberUtil.mul(totalSaleAmount, NumUtil.div(commissionRate, new BigDecimal(CommonDict.ONE_HUNDRED))).setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
        saleContractItem.setCommissionAmount(new JsonAmount().setAmount(calcCommissionAmount).setCurrency(currency));
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
            // 59<= 总体积 <= 68
        } else if (totalVolume.compareTo(FORTY_NINE_CABINET_THRESHOLD) > 0 && totalVolume.compareTo(FORTY_FOOT_CONTAINER_THRESHOLD) < 1) {
            computeValue(cabinetCache, totalVolume, FORTY_NINE_CABINET_THRESHOLD, FORTY_FOOT_CONTAINER);
            // 49<= 总体积 <= 59
        } else if (totalVolume.compareTo(FIFTY_NINE_THRESHOLD) > 0  && totalVolume.compareTo(FORTY_NINE_CABINET_THRESHOLD) < 1) {
            computeValue(cabinetCache, totalVolume, FIFTY_NINE_THRESHOLD, FORTY_FOOT_CABINET);
            // 29<= 总体积 <= 49
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
                BigDecimal bulkHandlingCost = NumberUtil.add(bulkHandlingStartFee, NumberUtil.mul(NumUtil.sub(bulkHandlingVolume,CalculationDict.ONE).setScale(CalculationDict.ZERO,RoundingMode.HALF_UP), bulkHandlingFee));
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

    private static void checkCabinet(Map<String, BigDecimal> cabinetMap, SaleContractSaveDTO saleContract) {
        if (CollUtil.isEmpty(cabinetMap)) {
            return;
        }
        // 散货体积
        BigDecimal bulkHandlingVolume = cabinetMap.get(BULK_HANDLING);
        saleContract.setBulkHandlingVolume(bulkHandlingVolume);
        // 20尺柜
        BigDecimal twentyFootCabinet = cabinetMap.get(TWENTY_FOOT_CABINET);
        saleContract.setTwentyFootCabinetNum(twentyFootCabinet.intValue());
        // 40尺柜
        BigDecimal fortyFootCabinet = cabinetMap.get(FORTY_FOOT_CABINET);
        saleContract.setFortyFootCabinetNum(fortyFootCabinet.intValue());
        // 40尺高柜
        BigDecimal fortyFootContainer = cabinetMap.get(FORTY_FOOT_CONTAINER);
        saleContract.setFortyFootContainerNum(fortyFootContainer.intValue());
    }

    /**
     * 校验毛重合计
     *
     * @param saleContractItemList 销售明细列表
     * @param totalGroussweight    前端传入的毛重合计
     */
    private static JsonWeight checkTotalGrossWeight(List<SaleContractItemSaveDTO> saleContractItemList, JsonWeight totalGroussweight) {
        if (CollUtil.isEmpty(saleContractItemList)) {
            return null;
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
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
        return new JsonWeight().setWeight(totalGroussweightCalc).setUnit(CalculationDict.KILOGRAM);
    }

    /**
     * 校验净重合计
     *
     * @param saleContractItemList 销售明细列表
     * @param totalWeight          前端传入的净重合计
     */
    private static JsonWeight checkTotalWeight(List<SaleContractItemSaveDTO> saleContractItemList, JsonWeight totalWeight) {
        if (CollUtil.isEmpty(saleContractItemList)) {
            return null;
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
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
        return new JsonWeight().setWeight(totalWeightCalc).setUnit(CalculationDict.KILOGRAM);
    }

    /**
     * 校验体积合计
     *
     * @param saleContractItemList 销售明细列表
     * @param totalVolume          前端传入的体积合计
     */
    private static BigDecimal chackTotalVolume(List<SaleContractItemSaveDTO> saleContractItemList, BigDecimal totalVolume) {
        if (CollUtil.isEmpty(saleContractItemList)) {
            return null;
        }
        BigDecimal totalVolumeCalc = saleContractItemList.stream()
                .map(s -> NumUtil.mul(CalcSpecificationUtil.calcSpecificationTotalVolume(s.getSpecificationList()), new BigDecimal(s.getBoxCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
       return totalVolumeCalc;

    }

    /**
     * 校验货值合计
     *
     * @param saleContractItemList 销售明细列表
     * @param totalGoodsValue      前端传入的货值合计
     */
    private static JsonAmount checkTotalGoodsValue(List<SaleContractItemSaveDTO> saleContractItemList, JsonAmount totalGoodsValue,String currency) {
        if (CollUtil.isEmpty(saleContractItemList)) {
            return null;
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
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
      return new JsonAmount().setAmount(totalGoodsValueCalc).setCurrency(currency);
    }


    /**
     * 校验采购合计
     *
     * @param saleContractItemList 销售明细列表
     * @param totalPurchase        前端传入的采购合计
     */
//    private static void checkTotalPurchase(List<SaleContractItemSaveDTO> saleContractItemList, List<JsonAmount> totalPurchase, Map<String, BigDecimal> currencyRateMap) {
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
//                .setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
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
    private static JsonAmount checkAccessoriesPurchaseTotal(List<SaleContractItemSaveDTO> saleContractItemList, JsonAmount accessoriesPurchaseTotal, Map<String, BigDecimal> currencyRateMap,String currency) {
        if (CollUtil.isEmpty(saleContractItemList)) {
            return null;
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
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
        return new JsonAmount().setAmount(totalPurchaseCalc).setCurrency(currency);
    }

    /**
     * 校验退税总额
     *
     * @param saleContractItemList 销售明细列表
     * @param totalVatRefund       前端传入的退税总额
     */
    private static JsonAmount checkTotalVatRefund(List<SaleContractItemSaveDTO> saleContractItemList, JsonAmount totalVatRefund,String currency) {
        if (CollUtil.isEmpty(saleContractItemList)) {
            return null;
        }
        BigDecimal totalVatRefundCalc = saleContractItemList.stream()
                .map(SaleContractItemSaveDTO::getTaxRefundPrice)
                .filter(Objects::nonNull)
                .map(JsonAmount::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
        return new JsonAmount().setAmount(totalVatRefundCalc).setCurrency(currency);
    }

    /**
     * 校验明细退税金额
     *
     * @param taxRefundRate        退税率
     * @param purchaseWithTaxPrice 含税单价
     * @param quantity             销售数量
     * @param taxRefundPrice       退税金额
     */
    private static JsonAmount checkItemVatRefund(BigDecimal taxRefundRate, JsonAmount purchaseWithTaxPrice,Integer quantity, JsonAmount taxRefundPrice, Map<String, BigDecimal> currencyRateMap,String currency) {
        if (Objects.isNull(taxRefundRate)) {
            return null;
        }
        if (Objects.isNull(purchaseWithTaxPrice)) {
            return null;
        }
        BigDecimal currencyRate = currencyRateMap.get(purchaseWithTaxPrice.getCurrency());
        if (currencyRate == null || BigDecimal.ZERO.compareTo(currencyRate) == 0) {
            throw exception(CONTRACT_CURRENCY_RATE_INVALID, purchaseWithTaxPrice.getCurrency());
        }
        // 将退税率转为百分数
        taxRefundRate = NumUtil.mul(taxRefundRate, new BigDecimal(CalculationDict.ONE_HUNDREDTH)).setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
        // 退税金额 = 退税金额=【含税总价*含税总价汇率/(1+退税率*100%)】*退税率*100%
        BigDecimal taxRefundPriceCalc = NumUtil.mul(NumUtil.div(NumUtil.mul(purchaseWithTaxPrice.getAmount(),BigDecimal.valueOf(quantity), currencyRate), NumUtil.add(new BigDecimal(CalculationDict.ONE), taxRefundRate)), taxRefundRate).setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
       return new JsonAmount().setAmount(taxRefundPriceCalc).setCurrency(currency);
    }

    /**
     * 校验平台费
     *
     * @param saleContractItemList 销售明细列表
     * @param currencyRateMap      币种汇率缓存
     * @param transformedConfigMap 系统配置缓存
     * @param platformFee          前端传入的平台费
     */
    private static JsonAmount checkPlatformFee(List<SaleContractItemSaveDTO> saleContractItemList, Map<String, BigDecimal> currencyRateMap, Map<String, BigDecimal> transformedConfigMap, JsonAmount platformFee,String currency) {
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
                .setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
       return new JsonAmount().setAmount(platformFeeCalc).setCurrency(currency);
    }

    /**
     * 校验佣金
     *
     * @param saleContractItemList 销售明细列表
     * @param commissionAmount     前端传入的佣金
     */
    private static JsonAmount checkCommissionAmount(List<SaleContractItemSaveDTO> saleContractItemList, JsonAmount commissionAmount,String currency) {
        BigDecimal commissionAmountCalc = saleContractItemList.stream()
                .map(SaleContractItemSaveDTO::getCommissionAmount)
                .filter(Objects::nonNull)
                .map(JsonAmount::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);

        return new JsonAmount().setAmount(commissionAmountCalc).setCurrency(currency);
    }

    /**
     * 校验箱数
     *
     * @param saleContractItem 销售合同明细
     * @return BigDecimal 箱数
     */
    private static BigDecimal checkBoxCount(SaleContractItemSaveDTO saleContractItem) {
        // 数量
        Integer quantity = saleContractItem.getQuantity();
        // 外箱装量
        Integer qtyPerOuterbox = saleContractItem.getQtyPerOuterbox();
        if (Objects.isNull(qtyPerOuterbox) || qtyPerOuterbox == 0) {
            throw exception(CURRENCY_ITEM_OUTERBOX_NUM, saleContractItem.getSkuCode());
        }
        // 箱数 取整
        return NumUtil.div(quantity, qtyPerOuterbox).setScale(CalculationDict.ZERO, RoundingMode.UP);
    }


    /**
     * 计算尺柜装量
     */
    public static Long getContainerCapacity(ContainerTypeEnum type, BigDecimal volume){
        if(Objects.equals(volume, BigDecimal.ZERO)){
            return (long)0;
        }
        BigDecimal total = BigDecimal.ZERO;
        if(ContainerTypeEnum.TWENTY == type){
            total = TWENTY_NINE_THRESHOLD;
        }
        if(ContainerTypeEnum.FORTY == type){
            total = FORTY_NINE_CABINET_THRESHOLD;
        }
        if(ContainerTypeEnum.FORTY_HIGH == type){
            total = FORTY_FOOT_CONTAINER_THRESHOLD;
        }
        BigDecimal divide = total.divide(volume,10, RoundingMode.HALF_UP);
        return divide.setScale(0,RoundingMode.FLOOR).longValue();

    }

    /**
     * 计算尺柜可装装量
     *  20#装量（29 / 外箱体积 * 外箱装量）、40#装量（59 / 外箱体积 * 外箱装量）、40HQ#装量（68 / 外箱体积 * 外箱装量）
     */
    public static Long getContainerContainNum(ContainerTypeEnum type, BigDecimal outerboxVolume, Integer qtyPerOuterbox){
        if(Objects.equals(outerboxVolume, BigDecimal.ZERO)){
            return (long)0;
        }
        BigDecimal total = BigDecimal.ZERO;
        if(ContainerTypeEnum.TWENTY == type){
            total = TWENTY_NINE_THRESHOLD;
        }
        if(ContainerTypeEnum.FORTY == type){
            total = FORTY_NINE_CABINET_THRESHOLD;
        }
        if(ContainerTypeEnum.FORTY_HIGH == type){
            total = FORTY_FOOT_CONTAINER_THRESHOLD;
        }
        BigDecimal divide = total.divide(outerboxVolume,10, RoundingMode.HALF_UP);
        return  NumberUtil.toBigDecimal(divide.setScale(0,RoundingMode.FLOOR)).multiply(NumberUtil.toBigDecimal(qtyPerOuterbox)).longValue();
    }
}
