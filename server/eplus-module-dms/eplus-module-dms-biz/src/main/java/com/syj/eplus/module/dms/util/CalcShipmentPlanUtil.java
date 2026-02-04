package com.syj.eplus.module.dms.util;

import cn.hutool.core.collection.CollUtil;
import com.syj.eplus.framework.common.dict.CalculationDict;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.util.CalcSpecificationUtil;
import com.syj.eplus.framework.common.util.NumUtil;
import com.syj.eplus.module.dms.controller.admin.shipmentplan.vo.ShipmentPlanSaveReqVO;
import com.syj.eplus.module.dms.dal.dataobject.shipmentplanitem.ShipmentPlanItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.dms.enums.ErrorCodeConstants.*;
import static com.syj.eplus.module.sms.enums.ErrorCodeConstants.SHIPMENT_PLAN_TOTAL_WEIGHT_EXCEPTION;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/8 16:55
 */
public class CalcShipmentPlanUtil {

    public static void checkItemCost(ShipmentPlanSaveReqVO shipmentPlanSaveReqVO, Map<String, String> configMap, Map<String, BigDecimal> currencyRateMap) {
        List<ShipmentPlanItem> oldShipmentPlanItemList = shipmentPlanSaveReqVO.getChildren();
        if (CollUtil.isEmpty(oldShipmentPlanItemList)) {
            return;
        }
        List<ShipmentPlanItem> shipmentPlanItemList = oldShipmentPlanItemList.stream().filter(s->s.getShippingQuantity()>0).toList();
        // 体积合计
        BigDecimal totalVolume = shipmentPlanSaveReqVO.getTotalVolume();
        if (Objects.isNull(totalVolume) || totalVolume.compareTo(BigDecimal.ZERO) == CalculationDict.ZERO) {
            throw exception(CONTRACT_ZERO_VOLUME);
        }
        // 计算明细费用
        checkItemFee(shipmentPlanItemList);
        // 货值合计
        checkTotalGoodsValue(shipmentPlanItemList, shipmentPlanSaveReqVO.getTotalGoodsValue());
        // 数量合计
        checkTotalQuantity(shipmentPlanItemList, shipmentPlanSaveReqVO.getTotalQuantity());
        // 箱数合计
        checkTotalBoxCount(shipmentPlanItemList, shipmentPlanSaveReqVO.getTotalBoxes());
        // 毛重合计
        checkTotalGrossWeight(shipmentPlanItemList, shipmentPlanSaveReqVO.getTotalGrossweight());
        // 净重合计
        checkTotalWeight(shipmentPlanItemList, shipmentPlanSaveReqVO.getTotalWeight());
        // 体积合计
        chackTotalVolume(shipmentPlanItemList, shipmentPlanSaveReqVO.getTotalVolume());
    }

    /**
     * 校验体积合计
     *
     * @param shipmentPlanItemList 出运明细列表
     * @param totalVolume          前端传入的体积合计
     */
    private static void chackTotalVolume(List<ShipmentPlanItem> shipmentPlanItemList, BigDecimal totalVolume) {
        if (CollUtil.isEmpty(shipmentPlanItemList)) {
            return;
        }
        BigDecimal totalVolumeCalc = shipmentPlanItemList.stream()
                .map(s -> NumUtil.mul(CalcSpecificationUtil.calcSpecificationTotalVolume(s.getSpecificationList()), new BigDecimal(s.getBoxCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(CalculationDict.DECIMAL_PRECISION, RoundingMode.HALF_UP)
                .setScale(CalculationDict.DECIMAL_CHECK, RoundingMode.HALF_UP);
        if (Objects.isNull(totalVolume) || !NumUtil.bigDecimalsEqual(totalVolumeCalc, totalVolume.setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP))) {
            throw exception(CONTRACT_TOTAL_VOLUME, totalVolumeCalc);
        }
    }

    /**
     * 校验净重合计
     *
     * @param shipmentPlanItemList 出运明细列表
     * @param totalWeight          前端传入的净重合计
     */
    private static void checkTotalWeight(List<ShipmentPlanItem> shipmentPlanItemList, JsonWeight totalWeight) {
        if (CollUtil.isEmpty(shipmentPlanItemList)) {
            return;
        }
        BigDecimal totalWeightCalc = shipmentPlanItemList.stream()
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
        if (Objects.isNull(totalWeight) || !NumUtil.bigDecimalsEqual(totalWeightCalc, totalWeight.getWeight().setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP))) {
            throw exception(CONTRACT_TOTAL_WEIGHT, totalWeightCalc);
        }
    }

    /**
     * 校验毛重合计
     *
     * @param shipmentPlanItemList 出运明细列表
     * @param totalGroussweight    前端传入的毛重合计
     */
    private static void checkTotalGrossWeight(List<ShipmentPlanItem> shipmentPlanItemList, JsonWeight totalGroussweight) {
        if (CollUtil.isEmpty(shipmentPlanItemList)) {
            return;
        }
        BigDecimal totalGroussweightCalc = shipmentPlanItemList.stream()
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
                .setScale(CalculationDict.DECIMAL_CHECK, RoundingMode.HALF_UP);
        if (Objects.isNull(totalGroussweight) || !NumUtil.bigDecimalsEqual(totalGroussweightCalc, totalGroussweight.getWeight().setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP))) {
            throw exception(CONTRACT_TOTAL_GROUSSWEIGHT, totalGroussweightCalc);
        }
    }

    /**
     * 校验箱数合计
     *
     * @param shipmentPlanItemList 出运明细列表
     * @param totalBoxes           前端传入箱数合计
     */
    private static void checkTotalBoxCount(List<ShipmentPlanItem> shipmentPlanItemList, Integer totalBoxes) {
        if (CollUtil.isEmpty(shipmentPlanItemList)) {
            return;
        }
        Integer calcTotalBoxes = shipmentPlanItemList.stream().map(ShipmentPlanItem::getBoxCount).reduce(0, Integer::sum);
        if (Objects.isNull(calcTotalBoxes) || calcTotalBoxes.intValue() != totalBoxes.intValue()) {
            throw exception(CONTRACT_TOTAL_GOODS_QUANTITY, calcTotalBoxes);
        }
    }

    /**
     * 校验数量合计
     *
     * @param shipmentPlanItemList 出运明细列表
     * @param totalQuantity        前端传入数量合计
     */
    private static void checkTotalQuantity(List<ShipmentPlanItem> shipmentPlanItemList, Integer totalQuantity) {
        if (CollUtil.isEmpty(shipmentPlanItemList)) {
            return;
        }
        Integer calcTotalQuantity = shipmentPlanItemList.stream().map(ShipmentPlanItem::getShippingQuantity).reduce(0, Integer::sum);
        if (calcTotalQuantity.intValue() != totalQuantity.intValue()) {
            throw exception(CONTRACT_TOTAL_GOODS_QUANTITY, calcTotalQuantity);
        }

    }

    /**
     * 校验明细金额
     *
     * @param shipmentPlanItemList 出运明细列表
     */
    private static void checkItemFee(List<ShipmentPlanItem> shipmentPlanItemList) {
        shipmentPlanItemList.forEach(shipmentPlanItem -> {
            // 校验明细佣金金额
//            checkCommissionFee(shipmentPlanItem);
            //校验明细退税金额
//            checkItemVatRefund(shipmentPlanItem.getTaxRefundRate(), shipmentPlanItem.getPurchaseWithTaxPrice(), shipmentPlanItem.getTaxRefundPrice());
            // 校验箱数
            checkBoxCount(shipmentPlanItem);
        });
    }


    /**
     * 校验箱数
     *
     * @param shipmentPlanItem 出运明细
     * @return BigDecimal 箱数
     */
    private static BigDecimal checkBoxCount(ShipmentPlanItem shipmentPlanItem) {
        // 数量
        Integer quantity = shipmentPlanItem.getShippingQuantity();
        // 外箱装量
        Integer qtyPerOuterbox = shipmentPlanItem.getQtyPerOuterbox();
        if (Objects.isNull(qtyPerOuterbox) || qtyPerOuterbox == 0) {
            throw exception(CURRENCY_ITEM_OUTERBOX_NUM, shipmentPlanItem.getSkuCode());
        }
        // 箱数 取整
        BigDecimal boxCount = NumUtil.div(quantity, qtyPerOuterbox).setScale(CalculationDict.ZERO,RoundingMode.UP);
        if (Objects.isNull(shipmentPlanItem.getBoxCount()) || !NumUtil.bigDecimalsEqual(boxCount, BigDecimal.valueOf(shipmentPlanItem.getBoxCount()))) {
            throw exception(CURRENCY_ITEM_BOX_COUNT, boxCount);
        }
        return boxCount;
    }

    /**
     * 校验货值合计
     *
     * @param items 出运明细列表
     * @param totalGoodsValue      前端传入的货值合计
     */
    private static void checkTotalGoodsValue(List<ShipmentPlanItem> items, List<JsonAmount> totalGoodsValue) {
        if (CollUtil.isEmpty(totalGoodsValue)||CollUtil.isEmpty(items)){
            return;
        }
        Map<String, BigDecimal> totalAmountMap = totalGoodsValue.stream().collect(Collectors.toMap(JsonAmount::getCurrency, JsonAmount::getAmount, (oldValue, newValue) -> oldValue));
        Map<String, List<JsonAmount>> result = items.stream().map(s->{
            BigDecimal amount = s.getSaleUnitPrice().getAmount();
            Integer shippingQuantity = s.getShippingQuantity();
            // 采购合计 = 采购含税价 * 出运数量
            return new JsonAmount().setAmount(NumUtil.mul(amount, BigDecimal.valueOf(shippingQuantity))).setCurrency(s.getSaleUnitPrice().getCurrency());
        }).collect(Collectors.groupingBy(JsonAmount::getCurrency));
        result.forEach((k,v)->{
            BigDecimal totalAmount = v.stream().map(JsonAmount::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal baseAmount = totalAmountMap.get(k);
            if (totalAmount.setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP).compareTo(baseAmount.setScale(CalculationDict.DECIMAL_CHECK,RoundingMode.HALF_UP))!=0){
                throw exception(CONTRACT_TOTAL_GOODS_VALUE);
            }
        });
    }


    /**
     * 计算采购合计
     * @param totalPurchase 前端传入的采购合计
     * @param items 出运明细
     */
    private static void calcPurchaseTotalAmount(List<JsonAmount> totalPurchase,List<ShipmentPlanItem> items){
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
                throw exception(SHIPMENT_PLAN_TOTAL_WEIGHT_EXCEPTION,totalAmount);
            }
        });
    }
}
