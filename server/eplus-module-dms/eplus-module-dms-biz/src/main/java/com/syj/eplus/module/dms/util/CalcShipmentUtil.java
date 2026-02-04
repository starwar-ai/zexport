package com.syj.eplus.module.dms.util;

import cn.hutool.core.collection.CollUtil;
import com.syj.eplus.framework.common.dict.CalculationDict;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.util.CalcSpecificationUtil;
import com.syj.eplus.framework.common.util.NumUtil;
import com.syj.eplus.module.dms.dal.dataobject.shipment.ShipmentDO;
import com.syj.eplus.module.dms.dal.dataobject.shipmentitem.ShipmentItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.dms.enums.ErrorCodeConstants.*;
import static com.syj.eplus.module.sms.enums.ErrorCodeConstants.CONTRACT_CURRENCY_RATE_INVALID;
import static com.syj.eplus.module.sms.enums.ErrorCodeConstants.SHIPMENT_PURCHASE_TOTAL_EXCEPTION;

public class CalcShipmentUtil {
    public static void calcItemCost(ShipmentDO shipmentDO, List<ShipmentItem> shipmentItemList,Map<String, BigDecimal> currencyRateMap) {
        if (CollUtil.isEmpty(shipmentItemList)) {
            return;
        }
        // 计算体积合计
        BigDecimal totalVolume = shipmentItemList.stream().map(ShipmentItem::getTotalVolume).reduce(BigDecimal::add).get();
        shipmentDO.setTotalVolume(totalVolume);
        if (totalVolume.compareTo(BigDecimal.ZERO) == CalculationDict.ZERO) {
            throw exception(CONTRACT_ZERO_VOLUME);
        }
        // 计算明细费用
        calcItemFee(shipmentItemList,currencyRateMap);
        // 货值合计
        calcGoodsValueTotalAmount( shipmentDO,shipmentItemList);
        //报关合计
        calcDeclarationTotalAmount(shipmentDO,shipmentItemList);
        //采购合计
        //calcPurchaseTotalAmount(shipmentDO.getTotalPurchase(),shipmentItemList);
        // 其他合计
        calcTotalQuantity(shipmentDO,shipmentItemList);
    }

    /**
     * 校验体积合计
     *
     * @param shipmentItemList 出运明细列表
     * @param totalVolume          前端传入的体积合计
     */
    private static void chackTotalVolume(List<ShipmentItem> shipmentItemList, BigDecimal totalVolume) {
        if (CollUtil.isEmpty(shipmentItemList)) {
            return;
        }
        BigDecimal totalVolumeCalc = shipmentItemList.stream()
                .map(s -> NumUtil.mul(CalcSpecificationUtil.calcSpecificationTotalVolume(s.getSpecificationList()), new BigDecimal(s.getBoxCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP)
                .setScale(CalculationDict.DECIMAL_CHECK, RoundingMode.HALF_UP);
        if (Objects.isNull(totalVolume) || !NumUtil.bigDecimalsEqual(totalVolumeCalc, totalVolume)) {
            throw exception(CONTRACT_TOTAL_VOLUME, totalVolumeCalc);
        }
    }

    /**
     * 校验净重合计
     *
     * @param shipmentItemList 出运明细列表
     * @param totalWeight          前端传入的净重合计
     */
    private static BigDecimal calcTotalWeight(List<ShipmentItem> shipmentItemList, JsonWeight totalWeight) {
        if (CollUtil.isEmpty(shipmentItemList)) {
            return BigDecimal.ZERO;
        }
        return shipmentItemList.stream()
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
                .setScale(CalculationDict.DECIMAL_CHECK, RoundingMode.HALF_UP);
    }

    /**
     * 校验毛重合计
     *
     * @param shipmentItemList 出运明细列表
     * @param totalGroussweight    前端传入的毛重合计
     */
    private static BigDecimal calcTotalGrossWeight(List<ShipmentItem> shipmentItemList, JsonWeight totalGroussweight) {
        if (CollUtil.isEmpty(shipmentItemList)) {
            return BigDecimal.ZERO;
        }
        return shipmentItemList.stream()
                .map(s -> {
                    List<JsonSpecificationEntity> specificationList = s.getSpecificationList();
                    JsonWeight outerboxGrossweight = CalcSpecificationUtil.calcSpecificationTotalGrossweight(specificationList);
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
                .setScale(CalculationDict.DECIMAL_CHECK, RoundingMode.HALF_UP);
    }

    /**
     * 校验箱数合计
     *
     * @param shipmentItemList 出运明细列表
     * @param totalBoxes           前端传入箱数合计
     */
    private static void calcTotalBoxCount(List<ShipmentItem> shipmentItemList, Integer totalBoxes) {
        if (CollUtil.isEmpty(shipmentItemList)) {
            return;
        }
        Integer calcTotalBoxes = shipmentItemList.stream().map(ShipmentItem::getBoxCount).reduce(0, Integer::sum);
        if (Objects.isNull(calcTotalBoxes) || calcTotalBoxes.intValue() != totalBoxes.intValue()) {
            throw exception(CONTRACT_TOTAL_GOODS_QUANTITY, calcTotalBoxes);
        }
    }

    /**
     * 校验数量合计
     *
     * @param ShipmentItemList 出运明细列表
     * @param totalQuantity        前端传入数量合计
     */
    private static void calcTotalQuantity(List<ShipmentItem> ShipmentItemList, Integer totalQuantity) {
        if (CollUtil.isEmpty(ShipmentItemList)) {
            return;
        }
        Integer calcTotalQuantity = ShipmentItemList.stream().map(ShipmentItem::getShippingQuantity).reduce(0, Integer::sum);
        if (calcTotalQuantity.intValue() != totalQuantity.intValue()) {
            throw exception(CONTRACT_TOTAL_GOODS_QUANTITY, calcTotalQuantity);
        }
    }

    /**
     * 校验明细金额
     *
     * @param shipmentItemList 出运明细列表
     */
    private static void calcItemFee(List<ShipmentItem> shipmentItemList,Map<String, BigDecimal> currencyRateMap) {
        shipmentItemList.forEach(shipmentItem -> {
            //校验明细退税金额
            calcItemVatRefund(shipmentItem,currencyRateMap);
        });
    }

    /**
     * 校验明细退税金额
     *
     * @param shipmentItem        出运明细
     */
    private static void calcItemVatRefund(ShipmentItem shipmentItem, Map<String, BigDecimal> currencyRateMap) {
        BigDecimal taxRefundRate = shipmentItem.getTaxRefundRate();
        JsonAmount purchaseWithTaxPrice = shipmentItem.getPurchaseWithTaxPrice();
        Integer quantity = shipmentItem.getShippingQuantity();
        if (Objects.isNull(taxRefundRate)) {
            return;
        }
        if (Objects.isNull(purchaseWithTaxPrice)) {
            return;
        }
        BigDecimal currencyRate = currencyRateMap.get(purchaseWithTaxPrice.getCurrency());
        if (currencyRate == null || BigDecimal.ZERO.compareTo(currencyRate) == 0) {
            throw exception(CONTRACT_CURRENCY_RATE_INVALID, purchaseWithTaxPrice.getCurrency());
        }
        // 将退税率转为百分数
        taxRefundRate = NumUtil.mul(taxRefundRate, new BigDecimal(CalculationDict.ONE_HUNDREDTH)).setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP);
        // 退税金额 = 退税金额=【含税总价*含税总价汇率/(1+退税率*100%)】*退税率*100%
        BigDecimal taxRefundPriceCalc = NumUtil.mul(NumUtil.div(NumUtil.mul(purchaseWithTaxPrice.getAmount(),BigDecimal.valueOf(quantity), currencyRate), NumUtil.add(new BigDecimal(CalculationDict.ONE), taxRefundRate)), taxRefundRate).setScale(CalculationDict.DECIMAL_CHECK, RoundingMode.HALF_UP);
        shipmentItem.setTaxRefundPrice(new JsonAmount().setAmount(taxRefundPriceCalc).setCurrency(purchaseWithTaxPrice.getCurrency()));
    }

    /**
     * 校验箱数
     *
     * @param shipmentItem 出运明细
     * @return BigDecimal 箱数
     */
    private static BigDecimal calcBoxCount(ShipmentItem shipmentItem) {
        // 数量
        Integer quantity = shipmentItem.getShippingQuantity();
        // 外箱装量
        Integer qtyPerOuterbox = shipmentItem.getQtyPerOuterbox();
        if (Objects.isNull(qtyPerOuterbox) || qtyPerOuterbox == 0) {
            throw exception(CURRENCY_ITEM_OUTERBOX_NUM, shipmentItem.getSkuCode());
        }
        // 箱数 取整
        BigDecimal boxCount = NumUtil.div(quantity, qtyPerOuterbox).setScale(CalculationDict.ZERO, RoundingMode.UP);
        if (Objects.isNull(shipmentItem.getBoxCount()) || !NumUtil.bigDecimalsEqual(boxCount.setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP), BigDecimal.valueOf(shipmentItem.getBoxCount()).setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP))) {
            throw exception(CURRENCY_ITEM_BOX_COUNT, boxCount);
        }
        return boxCount;
    }

    /**
     * 计算报关合计
     * @param shipmentDO 出运明细
     * @param items 出运明细
     */
    private static void calcDeclarationTotalAmount(ShipmentDO shipmentDO, List<ShipmentItem> items){
        List<JsonAmount> totalDeclaration = shipmentDO.getTotalDeclaration();
        if (CollUtil.isEmpty(totalDeclaration)||CollUtil.isEmpty(items)){
            return;
        }
        Map<String, BigDecimal> totalAmountMap = totalDeclaration.stream().collect(Collectors.toMap(JsonAmount::getCurrency, JsonAmount::getAmount, (oldValue, newValue) -> oldValue));
        Map<String, List<JsonAmount>> result = items.stream().map(s->{
            BigDecimal amount = s.getDeclarationUnitPrice().getAmount();
            Integer declarationQuantity = s.getDeclarationQuantity();
            // 报关合计 = 销售价 * 报关数量
            return new JsonAmount().setAmount(NumUtil.mul(amount, BigDecimal.valueOf(declarationQuantity))).setCurrency(s.getSaleUnitPrice().getCurrency());
        }).collect(Collectors.groupingBy(JsonAmount::getCurrency));
        List<JsonAmount> amountList = result.entrySet().stream().map(entry -> new JsonAmount().setAmount(entry.getValue().stream().map(JsonAmount::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add)).setCurrency(entry.getKey())).toList();
        shipmentDO.setTotalDeclaration(amountList);
    }
    /**
     * 计算货值合计
     * @param shipmentDO 出运明细
     * @param items 出运明细
     */
    private static void calcGoodsValueTotalAmount(ShipmentDO shipmentDO, List<ShipmentItem> items){
        List<JsonAmount> totalGoodsValue = shipmentDO.getTotalGoodsValue();
        if (CollUtil.isEmpty(totalGoodsValue)||CollUtil.isEmpty(items)){
            return;
        }
        Map<String, List<JsonAmount>> result = items.stream().map(s->{
            BigDecimal amount = s.getSaleUnitPrice().getAmount();
            Integer shippingQuantity = s.getShippingQuantity();
            // 货值合计 = 销售价 * 出运数量
            return new JsonAmount().setAmount(NumUtil.mul(amount, BigDecimal.valueOf(shippingQuantity))).setCurrency(s.getSaleUnitPrice().getCurrency());
        }).collect(Collectors.groupingBy(JsonAmount::getCurrency));
        List<JsonAmount> amountList = result.entrySet().stream().map(entry -> new JsonAmount().setAmount(entry.getValue().stream().map(JsonAmount::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add)).setCurrency(entry.getKey())).toList();
        shipmentDO.setTotalGoodsValue(amountList);
    }




    /**
     * 计算采购合计
     * @param totalPurchase 前端传入的采购合计
     * @param items 出运明细
     */
    private static void calcPurchaseTotalAmount(List<JsonAmount> totalPurchase,List<ShipmentItem> items){
        if (CollUtil.isEmpty(totalPurchase)||CollUtil.isEmpty(items)){
            return;
        }
        Map<String, BigDecimal> totalAmountMap = totalPurchase.stream().collect(Collectors.toMap(JsonAmount::getCurrency, JsonAmount::getAmount, (oldValue, newValue) -> oldValue));
        Map<String, List<JsonAmount>> result = items.stream().map(s->{
            BigDecimal amount = s.getPurchaseWithTaxPrice().getAmount();
            Integer shippingQuantity = s.getShippingQuantity();
            // 采购合计 = 采购含税价 * 出运数量
            return new JsonAmount().setAmount(NumUtil.mul(amount, BigDecimal.valueOf(shippingQuantity))).setCurrency(s.getPurchaseWithTaxPrice().getCurrency());
        }).collect(Collectors.groupingBy(JsonAmount::getCurrency));
        result.forEach((k,v)->{
            BigDecimal totalAmount = v.stream().map(JsonAmount::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal baseAmount = totalAmountMap.get(k);
            if (totalAmount.setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP).compareTo(baseAmount.setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP))!=0){
                throw exception(SHIPMENT_PURCHASE_TOTAL_EXCEPTION,totalAmount);
            }
        });
    }

    /**
     * 校验退税合计
     *
     * @param shipmentItemList 出运明细列表
     * @param totalTaxRefundPrice      前端传入的退税合计
     */
    private static void calcTotalTaxRefundPrice(List<ShipmentItem> shipmentItemList, JsonAmount totalTaxRefundPrice) {
        if (CollUtil.isEmpty(shipmentItemList)) {
            return;
        }
        BigDecimal totalTaxRefundPriceCalc = shipmentItemList.stream()
                .map(s -> {
                    JsonAmount amount = s.getTaxRefundPrice();
                    if (Objects.isNull(amount)) {
                        return null;
                    }
                    return amount.getAmount();
                })
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(CalculationDict.DECIMAL_CHECK, RoundingMode.HALF_UP);
        if (Objects.isNull(totalTaxRefundPrice) || !NumUtil.bigDecimalsEqual(totalTaxRefundPriceCalc, totalTaxRefundPrice.getAmount())) {
            throw exception(CONTRACT_TOTAL_VAT_REFUND, totalTaxRefundPriceCalc);
        }
    }


    /**
     * 佣金合计
     *
     * @param shipmentItemList 出运明细列表
     * @param commissionAmount      前端传入的佣金合计
     */
    private static void calcCommissionAmount(List<ShipmentItem> shipmentItemList, JsonAmount commissionAmount) {
        if (CollUtil.isEmpty(shipmentItemList)) {
            return;
        }
        BigDecimal commissionAmountCalc = shipmentItemList.stream()
                .map(s -> {
                    JsonAmount amount = s.getCommissionAmount();
                    if (Objects.isNull(amount)) {
                        return null;
                    }
                    return amount.getAmount();
                })
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(CalculationDict.DECIMAL_CHECK, RoundingMode.HALF_UP);
        if (Objects.isNull(commissionAmount) || !NumUtil.bigDecimalsEqual(commissionAmountCalc, commissionAmount.getAmount().setScale(CalculationDict.DECIMAL_CHECK, RoundingMode.HALF_UP))) {
            throw exception(CONTRACT_TOTAL_COMMISSIONAMOUNT, commissionAmountCalc);
        }
    }


    /**
     * 保险费合计
     *
     * @param shipmentItemList 出运明细列表
     * @param insuranceFee      前端传入的佣金合计
     */
    private static void calcInsuranceFee(List<ShipmentItem> shipmentItemList, JsonAmount insuranceFee) {
        if (CollUtil.isEmpty(shipmentItemList)) {
            return;
        }
        BigDecimal insuranceFeeCalc = shipmentItemList.stream()
                .map(s -> {
                    JsonAmount amount = s.getInsuranceFee();
                    if (Objects.isNull(amount)) {
                        return null;
                    }
                    return amount.getAmount();
                })
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(CalculationDict.DECIMAL_CHECK, RoundingMode.HALF_UP);
        if (Objects.isNull(insuranceFee) || !NumUtil.bigDecimalsEqual(insuranceFeeCalc, insuranceFee.getAmount().setScale(CalculationDict.DECIMAL_CHECK, RoundingMode.HALF_UP))) {
            throw exception(CONTRACT_TOTAL_INSURANCEFEE, insuranceFeeCalc);
        }
    }

    /**
     * 计算正常统计的合计
     * @param updateReqVO 出运单
     * @param children 出运明细
     */
    private static void calcTotalQuantity(ShipmentDO updateReqVO, List<ShipmentItem> children){
        // 数量合计
        Integer totalQuantity = updateReqVO.getTotalQuantity();
        children.stream().map(ShipmentItem::getShippingQuantity).reduce(Integer::sum).ifPresent(updateReqVO::setTotalQuantity);
        // 箱数合计
        children.stream().map(ShipmentItem::getBoxCount).reduce(Integer::sum).ifPresent(updateReqVO::setTotalBoxes);
        // 外箱体积 * 箱数
        BigDecimal calcTotalVolume = children.stream().map(s-> CalcSpecificationUtil.calcSpecificationTotalVolume(s.getSpecificationList()).multiply(BigDecimal.valueOf(s.getBoxCount()))).reduce(BigDecimal.ZERO, BigDecimal::add);
        updateReqVO.setTotalVolume(calcTotalVolume);
        // 总毛重
        JsonWeight totalGrossweightJson = updateReqVO.getTotalGrossweight();
        BigDecimal totalGrossWeight = calcTotalGrossWeight(children, totalGrossweightJson);
        updateReqVO.setTotalGrossweight(totalGrossweightJson.setWeight(totalGrossWeight));
        // 总净重
        JsonWeight totalWeightJson = updateReqVO.getTotalWeight();
        BigDecimal totalWeight = calcTotalWeight(children, totalWeightJson);
        updateReqVO.setTotalWeight(totalWeightJson.setWeight(totalWeight));
    }
}
