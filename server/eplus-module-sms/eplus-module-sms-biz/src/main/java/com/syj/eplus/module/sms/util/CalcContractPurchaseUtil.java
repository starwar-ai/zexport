package com.syj.eplus.module.sms.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import com.syj.eplus.framework.common.dict.CalculationDict;
import com.syj.eplus.framework.common.dict.SaleContractDict;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.framework.common.enums.CalclationTypeEnum;
import com.syj.eplus.framework.common.enums.SkuTypeEnum;
import com.syj.eplus.framework.common.util.CalcSpecificationUtil;
import com.syj.eplus.framework.common.util.NumUtil;
import com.syj.eplus.module.crm.enums.cust.TransportTypeEnum;
import com.syj.eplus.module.sms.dal.dataobject.addsubitem.AddSubItem;
import com.syj.eplus.module.sms.dal.dataobject.salecontract.SaleContractDO;
import com.syj.eplus.module.sms.dal.dataobject.salecontractitem.SaleContractItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.framework.common.dict.SaleContractDict.*;
import static com.syj.eplus.module.sms.enums.ErrorCodeConstants.CURRENCY_ITEM_OUTERBOX_NUM;

public class CalcContractPurchaseUtil {

    public static void CalcContractPurchase(SaleContractDO saleContract, Map<String, String> configMap, Map<String, BigDecimal>  currencyRateMap, List<SaleContractItem> saleContractItemList, List<AddSubItem> addSubItemList) {
        if (CollUtil.isEmpty(saleContractItemList)) {
            return;
        }
        String currency = saleContract.getCurrency();
        saleContractItemList.forEach(s -> {
            // 计算明细退税金额
            JsonAmount realTaxRefundPrice = calcItemVatRefund(s.getRealTaxRefundRate(), s.getRealPurchaseWithTaxPrice(), s.getQuantity(), currencyRateMap, currency);
            s.setRealTaxRefundPrice(realTaxRefundPrice);
        });
        // 计算真实订单毛利、毛利率
        calcSaleContractOrderGrossProfit(saleContract, currencyRateMap, saleContractItemList, addSubItemList);
        // 计算采购总金额
        checkTotalPurchase(saleContract, saleContractItemList, currencyRateMap);
        // 计算体积
        BigDecimal realTotalVolume = chackTotalVolume(saleContractItemList);
        if (Objects.isNull(realTotalVolume) || realTotalVolume.compareTo(BigDecimal.ZERO) == CalculationDict.ZERO) {
           return;
        }
        saleContract.setRealTotalVolume(realTotalVolume);
        // 将系统配重中value转为可执行计算的decimal
        Map<String, BigDecimal> transformedConfigMap = CalcContactUtil.convertConfigValue(configMap);
        // 表单拖柜费
        JsonAmount trailerFee = Objects.isNull(saleContract.getTrailerFee()) ? new JsonAmount().setAmount(BigDecimal.ZERO) : saleContract.getTrailerFee();
        // 表单预估总费用
        BigDecimal totalCabinetFee = BigDecimal.ZERO;
        if (TransportTypeEnum.OCEAN_SHIPPING.getValue().equals(saleContract.getTransportType())) {
            // 计算拖柜数量
            Map<String, BigDecimal> cabinetMap = CalcContactUtil.calcCabinetNum(realTotalVolume, configMap);
            // 计算真实尺柜数量
            calcCabinet(cabinetMap, saleContract);
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
            if (bulkHandlingVolume.compareTo(BigDecimal.ZERO) > 0) {
                bulkHandlingCost = bulkHandlingStartFee;
                if (bulkHandlingVolume.compareTo(BigDecimal.ONE) > 0) {
                    bulkHandlingCost = NumberUtil.add(bulkHandlingCost, NumberUtil.mul(NumUtil.sub(bulkHandlingVolume, BigDecimal.ONE), bulkHandlingFee));
                }
            }
            // 尺柜总费用
            totalCabinetFee = NumberUtil.add(NumberUtil.mul(new BigDecimal(twentyFootCabinetNum), twentyFootCabinetFee),
                    NumberUtil.mul(new BigDecimal(fortyFootCabinetNum), fortyFootCabinetFee),
                    NumberUtil.mul(new BigDecimal(fortyFootContainerNum), fortyFootContainerFee),
                    bulkHandlingCost);
        }
        totalCabinetFee = NumberUtil.add(totalCabinetFee, trailerFee.getAmount());
        saleContract.setRealEstimatedTotalFreight(new JsonAmount().setAmount(totalCabinetFee).setCurrency(currency));
        // 计算明细费用
        calcItemFee(saleContractItemList, totalCabinetFee, realTotalVolume, trailerFee, currencyRateMap, currency);
        // 箱数合计
        Integer realTotalBoxes = saleContractItemList.stream().map(SaleContractItem::getRealBoxCount).reduce(Integer::sum).get();
        saleContract.setRealTotalBoxes(realTotalBoxes);
        // 毛重合计
        JsonWeight grossWeight = calcTotalGrossWeight(saleContractItemList);
        saleContract.setRealTotalGrossweight(grossWeight);
        // 净重合计
        JsonWeight totalWeight = calcTotalWeight(saleContractItemList);
        saleContract.setRealTotalWeight(totalWeight);
        // 货值合计
        JsonAmount totalGoodsValue = CalcContactUtil.checkTotalGoodsValue(saleContractItemList, saleContract.getTotalGoodsValue(), currency);
        saleContract.setTotalGoodsValue(totalGoodsValue);
        // 采购合计
        checkTotalPurchase(saleContract, saleContractItemList, currencyRateMap);
        // 配件采购合计
        JsonAmount accessoriesPurchaseTotal = calcAccessoriesPurchaseTotal(saleContract, saleContractItemList, currencyRateMap);
        saleContract.setRealAccessoriesPurchaseTotal(accessoriesPurchaseTotal);
        // 退税总额
        JsonAmount totalVatRefund = calcTotalVatRefund(saleContractItemList);
        saleContract.setRealTotalVatRefund(totalVatRefund);
        // 应收汇款
        saleContract.setReceivableExchange(saleContract.getTotalGoodsValue());
    }

    /**
     * 计算体积合计
     *
     * @param saleContractItemList 销售明细列表
     */
    private static BigDecimal chackTotalVolume(List<SaleContractItem> saleContractItemList) {
        if (CollUtil.isEmpty(saleContractItemList)) {
            return null;
        }
        return saleContractItemList.stream()
                .map(s -> NumUtil.mul(CalcSpecificationUtil.calcSpecificationTotalVolume(s.getRealSpecificationList()), new BigDecimal(s.getBoxCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                ;

    }

    /**
     * 计算明细退税金额
     *
     * @param taxRefundRate            退税率
     * @param realPurchaseWithTaxPrice 含税单价
     * @param quantity                 销售数量
     */
    private static JsonAmount calcItemVatRefund(BigDecimal taxRefundRate, JsonAmount realPurchaseWithTaxPrice, Integer quantity, Map<String, BigDecimal> currencyRateMap, String currency) {
        if (Objects.isNull(taxRefundRate)) {
            return null;
        }
        if (Objects.isNull(realPurchaseWithTaxPrice)) {
            return null;
        }
        BigDecimal currencyRate = currencyRateMap.get(realPurchaseWithTaxPrice.getCurrency());
        // 将退税率转为百分数
        taxRefundRate = NumUtil.mul(taxRefundRate, new BigDecimal(CalculationDict.ONE_HUNDREDTH));
        // 退税金额 = 退税金额=【含税总价*含税总价汇率/(1+退税率*100%)】*退税率*100%
        BigDecimal taxRefundPriceCalc = NumUtil.mul(NumUtil.div(NumUtil.mul(realPurchaseWithTaxPrice.getAmount(), BigDecimal.valueOf(quantity), currencyRate), NumUtil.add(new BigDecimal(CalculationDict.ONE), taxRefundRate)), taxRefundRate);
        return new JsonAmount().setAmount(taxRefundPriceCalc).setCurrency(CalculationDict.CURRENCY_RMB);
    }

    /**
     * 计算订单毛利，毛利率
     *
     * @param saleContractDO       销售合同
     * @param currencyRateMap      币种汇率
     * @param saleContractItemList 销售明细
     * @param addSubItemList       加减项
     */
    public static void calcSaleContractOrderGrossProfit(SaleContractDO saleContractDO, Map<String, BigDecimal> currencyRateMap, List<SaleContractItem> saleContractItemList, List<AddSubItem> addSubItemList) {
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
                    ;
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
                    ;
            deductionAmount.setAmount(deductionAmountCalc);
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
                ;
        BigDecimal totalAmountCalc = NumberUtil.mul(saleContractDO.getTotalGoodsValue().getAmount(), currencyRateMap.get(saleContractDO.getTotalGoodsValue().getCurrency()))
                .add(NumberUtil.mul(saleContractDO.getAdditionAmount().getAmount(), currencyRateMap.get(saleContractDO.getAdditionAmount().getCurrency())))
                .subtract(NumberUtil.mul(saleContractDO.getDeductionAmount().getCheckAmount(), currencyRateMap.get(saleContractDO.getDeductionAmount().getCurrency())))
                .subtract(subCommissionAmount)
                ;
        JsonAmount totalAmount = new JsonAmount();
        totalAmount.setCurrency("RMB");
        totalAmount.setAmount(totalAmountCalc);
        BigDecimal totalAmountUsdCalc = NumberUtil.div(totalAmountCalc, currencyRateMap.get("USD")).setScale(CalculationDict.DECIMAL_CHECK, RoundingMode.HALF_UP);
        JsonAmount totalAmountUsd = new JsonAmount();
        totalAmountUsd.setCurrency("USD");
        totalAmountUsd.setAmount(totalAmountUsdCalc);
        // 收入
        BigDecimal addAmount = NumberUtil.mul(saleContractDO.getTotalGoodsValue().getAmount(), currencyRateMap.get(saleContractDO.getTotalGoodsValue().getCurrency()))
                .add(NumberUtil.mul(saleContractDO.getTotalVatRefund().getAmount(), currencyRateMap.get(saleContractDO.getTotalVatRefund().getCurrency())))
                .add(NumberUtil.mul(saleContractDO.getAdditionAmount().getAmount(), currencyRateMap.get(saleContractDO.getAdditionAmount().getCurrency())))
                ;
        BigDecimal totalPurchaseCalc = saleContractDO.getTotalPurchase().stream()
                .map(s -> {
                    BigDecimal amount = s.getAmount();
                    return NumUtil.mul(amount, currencyRateMap.get(s.getCurrency()));
                })
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                ;
        BigDecimal subAmount = totalPurchaseCalc
                .add(NumberUtil.mul(saleContractDO.getEstimatedPackingMaterials().getAmount(), currencyRateMap.get(saleContractDO.getEstimatedPackingMaterials().getCurrency())))
                .add(NumberUtil.mul(saleContractDO.getAccessoriesPurchaseTotal().getAmount(), currencyRateMap.get(saleContractDO.getAccessoriesPurchaseTotal().getCurrency())))
                .add(NumberUtil.mul(saleContractDO.getEstimatedTotalFreight().getAmount(), currencyRateMap.get(saleContractDO.getEstimatedTotalFreight().getCurrency())))
                .add(NumberUtil.mul(saleContractDO.getInspectionFee().getAmount(), currencyRateMap.get(saleContractDO.getInspectionFee().getCurrency())))
                .add(NumberUtil.mul(saleContractDO.getPlatformFee().getAmount(), currencyRateMap.get(saleContractDO.getPlatformFee().getCurrency())))
                .add(NumberUtil.mul(saleContractDO.getDeductionAmount().getAmount(), currencyRateMap.get(saleContractDO.getDeductionAmount().getCurrency())))
                .add(NumberUtil.mul(saleContractDO.getInsuranceFee().getAmount(), currencyRateMap.get(saleContractDO.getInsuranceFee().getCurrency())))
                .add(NumberUtil.mul(saleContractDO.getSinosureFee().getAmount(), currencyRateMap.get(saleContractDO.getSinosureFee().getCurrency())))
                .add(NumberUtil.mul(saleContractDO.getCommission().getAmount(), currencyRateMap.get(saleContractDO.getCommission().getCurrency())))
                .add(subCommissionAmount)
                ;
        //毛利润 = 收入 - 成本
        BigDecimal orderGrossProfitCalc = addAmount.subtract(subAmount);
        JsonAmount orderGrossProfit = new JsonAmount();
        orderGrossProfit.setAmount(orderGrossProfitCalc);
        orderGrossProfit.setCurrency("RMB");
        saleContractDO.setRealOrderGrossProfit(orderGrossProfit);
        //毛利率=毛利润/收入
        BigDecimal grossProfitMargin = BigDecimal.ZERO.compareTo(addAmount) == 0 ?BigDecimal.ZERO:NumberUtil.div(orderGrossProfitCalc, addAmount);
        saleContractDO.setRealGrossProfitMargin(grossProfitMargin);
    }

    /**
     * 计算采购总金额
     *
     * @param saleContractItemList 销售明细列表
     */
    private static void checkTotalPurchase(SaleContractDO saleContractDO, List<SaleContractItem> saleContractItemList, Map<String, BigDecimal> currencyRateMap) {
        if (CollUtil.isEmpty(saleContractItemList)) {
            return;
        }
        BigDecimal totalPurchaseCalc = saleContractItemList.stream()
                .map(s -> {
                    JsonAmount realPurchaseWithTaxPrice = s.getRealPurchaseWithTaxPrice();
                    if (Objects.isNull(realPurchaseWithTaxPrice)) {
                        return null;
                    }
                    BigDecimal amount = realPurchaseWithTaxPrice.getAmount();
                    Integer quantity = s.getRealPurchaseQuantity();
                    return NumUtil.mul(NumUtil.mul(amount, currencyRateMap.get(realPurchaseWithTaxPrice.getCurrency())), new BigDecimal(quantity));
                })
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        saleContractDO.setRealPurchaseTotal(new JsonAmount().setAmount(totalPurchaseCalc).setCurrency(CalculationDict.CURRENCY_RMB));
    }

    /**
     * 计算真实尺柜数量/散货体积
     *
     * @param cabinetMap   尺柜/散货数量map
     * @param saleContract 销售合同
     */

    private static void calcCabinet(Map<String, BigDecimal> cabinetMap, SaleContractDO saleContract) {
        if (CollUtil.isEmpty(cabinetMap)) {
            return;
        }
        // 散货体积
        BigDecimal bulkHandlingVolume = cabinetMap.get(BULK_HANDLING);
        saleContract.setRealBulkHandlingVolume(bulkHandlingVolume);
        // 20尺柜
        BigDecimal twentyFootCabinet = cabinetMap.get(TWENTY_FOOT_CABINET);
        saleContract.setRealTwentyFootCabinetNum(twentyFootCabinet.intValue());
        // 40尺柜
        BigDecimal fortyFootCabinet = cabinetMap.get(FORTY_FOOT_CABINET);
        saleContract.setRealFortyFootCabinetNum(fortyFootCabinet.intValue());
        // 40尺高柜
        BigDecimal fortyFootContainer = cabinetMap.get(FORTY_FOOT_CONTAINER);
        saleContract.setRealFortyFootContainerNum(fortyFootContainer.intValue());
    }

    /**
     * 计算毛重合计
     *
     * @param saleContractItemList 销售明细列表
     */
    private static JsonWeight calcTotalGrossWeight(List<SaleContractItem> saleContractItemList) {
        if (CollUtil.isEmpty(saleContractItemList)) {
            return null;
        }
        BigDecimal totalGroussweightCalc = saleContractItemList.stream()
                .map(s -> {
                    JsonWeight outerboxGrossweight = CalcSpecificationUtil.calcSpecificationTotalGrossweight(s.getRealSpecificationList());
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
        return new JsonWeight().setWeight(totalGroussweightCalc).setUnit(CalculationDict.KILOGRAM);
    }

    /**
     * 计算净重合计
     *
     * @param saleContractItemList 销售明细列表
     */
    private static JsonWeight calcTotalWeight(List<SaleContractItem> saleContractItemList) {
        if (CollUtil.isEmpty(saleContractItemList)) {
            return null;
        }
        BigDecimal totalWeightCalc = saleContractItemList.stream()
                .map(s -> {
                    JsonWeight outerboxNetweight = CalcSpecificationUtil.calcSpecificationTotalNetweight(s.getRealSpecificationList());
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
        return new JsonWeight().setWeight(totalWeightCalc).setUnit(CalculationDict.KILOGRAM);
    }

    /**
     * 计算配件采购合计
     *
     * @param saleContractItemList 销售明细列表
     */
    private static JsonAmount calcAccessoriesPurchaseTotal(SaleContractDO saleContractDO, List<SaleContractItem> saleContractItemList, Map<String, BigDecimal> currencyRateMap) {
        if (CollUtil.isEmpty(saleContractItemList)) {
            return null;
        }
        BigDecimal totalPurchaseCalc = saleContractItemList.stream()
                .filter(s -> SkuTypeEnum.ACCESSORIES.getValue().equals(s.getSkuType()))
                .map(s -> {
                    JsonAmount totalSaleAmount = s.getRealPurchaseWithTaxPrice();
                    if (Objects.isNull(totalSaleAmount)) {
                        return null;
                    }
                    BigDecimal amount = totalSaleAmount.getAmount();
                    Integer quantity = s.getRealPurchaseQuantity();
                    return NumUtil.mul(NumUtil.mul(amount, currencyRateMap.get(totalSaleAmount.getCurrency())), new BigDecimal(quantity));
                })
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                ;
        return new JsonAmount().setAmount(totalPurchaseCalc).setCurrency(CalculationDict.CURRENCY_RMB);
    }

    /**
     * 计算退税总额
     *
     * @param saleContractItemList 销售明细列表
     */
    private static JsonAmount calcTotalVatRefund(List<SaleContractItem> saleContractItemList) {
        if (CollUtil.isEmpty(saleContractItemList)) {
            return null;
        }
        BigDecimal totalVatRefundCalc = saleContractItemList.stream()
                .map(SaleContractItem::getRealTaxRefundPrice)
                .filter(Objects::nonNull)
                .map(JsonAmount::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                ;
        return new JsonAmount().setAmount(totalVatRefundCalc).setCurrency(CalculationDict.CURRENCY_RMB);
    }

    /**
     * 计算明细金额
     *
     * @param saleContractItemList  销售明细列表
     * @param estimatedTotalFreight 表单预估总费用
     * @param totalVolume           体积合计
     * @param trailerFee            表单拖柜费
     */
    private static void calcItemFee(List<SaleContractItem> saleContractItemList, BigDecimal estimatedTotalFreight, BigDecimal totalVolume, JsonAmount trailerFee, Map<String, BigDecimal> currencyRateMap, String currency) {
        // 表单拖柜费/体积合计
        BigDecimal trailerFeeVolumeDiv;
        if (Objects.nonNull(trailerFee)) {
            // 表单拖柜费
            BigDecimal trailerFeeAmount = trailerFee.getAmount();
            trailerFeeVolumeDiv = NumberUtil.div(trailerFeeAmount, totalVolume);
        } else {
            trailerFeeVolumeDiv = BigDecimal.ZERO;
        }
        saleContractItemList.forEach(saleContractItem -> {
            // 校验箱数
            BigDecimal boxCount = calcBoxCount(saleContractItem);
            saleContractItem.setRealBoxCount(boxCount.intValue());
            // 外箱体积
            BigDecimal outerBoxVolume = CalcSpecificationUtil.calcSpecificationTotalVolume(saleContractItem.getSpecificationList());
            // 外箱体积 * 箱数
            BigDecimal outerBoxVolumeTimesQuantity = NumberUtil.mul(outerBoxVolume, boxCount);
            // 明细预估总费用 = (表单预估总费用/体积合计)*(外箱体积*箱数)
            BigDecimal itemEstimatedTotalFreight = NumberUtil.div(NumberUtil.mul(estimatedTotalFreight, outerBoxVolumeTimesQuantity), totalVolume);
            saleContractItem.setForecastTotalCost(new JsonAmount().setAmount(itemEstimatedTotalFreight).setCurrency(currency));
            // 明细拖柜费 = (表单拖柜费/体积合计)*(外箱体积*箱数)
            BigDecimal itemTrailerFee = NumberUtil.mul(trailerFeeVolumeDiv, outerBoxVolumeTimesQuantity);
            saleContractItem.setTrailerFee(new JsonAmount().setAmount(itemTrailerFee).setCurrency(currency));
            // 校验明细佣金金额
            CalcContactUtil.checkCommissionFee(saleContractItem, currency);
            //校验明细退税金额
            JsonAmount taxRefundPrice;
            if (Objects.isNull(saleContractItem.getRealTaxRefundRate())||saleContractItem.getRealTaxRefundRate().compareTo(BigDecimal.ZERO)==0){
                taxRefundPrice = calcItemVatRefund(saleContractItem.getTaxRefundRate(), saleContractItem.getRealPurchaseWithTaxPrice(), saleContractItem.getQuantity(), currencyRateMap, currency);
            }else {
                taxRefundPrice = calcItemVatRefund(saleContractItem.getRealTaxRefundRate(), saleContractItem.getRealPurchaseWithTaxPrice(), saleContractItem.getQuantity(), currencyRateMap, currency);
            }

            saleContractItem.setRealTaxRefundPrice(taxRefundPrice);
        });
    }

    /**
     * 计算箱数
     *
     * @param saleContractItem 销售合同明细
     * @return BigDecimal 箱数
     */
    private static BigDecimal calcBoxCount(SaleContractItem saleContractItem) {
        // 数量
        Integer quantity = saleContractItem.getQuantity();
        // 外箱装量
        Integer qtyPerOuterbox = saleContractItem.getQtyPerOuterbox();
        if (Objects.isNull(qtyPerOuterbox) || qtyPerOuterbox == 0) {
            throw exception(CURRENCY_ITEM_OUTERBOX_NUM, saleContractItem.getSkuCode());
        }
        List<JsonSpecificationEntity> realSpecificationList = saleContractItem.getRealSpecificationList();
        int size = CollUtil.isEmpty(realSpecificationList) ? 1 : realSpecificationList.size();
        // 箱数 取整
        return NumUtil.mul(NumUtil.div(quantity, qtyPerOuterbox), size).setScale(CalculationDict.ZERO, RoundingMode.UP);
    }
}
