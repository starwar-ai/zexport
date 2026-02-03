//package com.syj.eplus.module.scm.util;
//
//import cn.hutool.core.util.NumberUtil;
//import cn.iocoder.yudao.framework.common.exception.ServiceException;
//import com.syj.eplus.module.scm.dal.dataobject.purchaseplanproduct.PurchasePlanProductDO;
//import com.syj.eplus.module.scm.enums.FreightEnum;
//import org.junit.Test;
//import org.springframework.util.CollectionUtils;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//import static com.syj.eplus.module.scm.enums.ErrorCodeConstants.PURCHASE_PLAN_PRODUCT_EMPTY;
//import static com.syj.eplus.module.scm.enums.FreightEnum.ALLOT_BY_NONE;
//
///**
// * Desc——金额分配工具类
// * Create by Rangers at  2024-04-12 10:27
// */
//public class AllotUtil {
//
//    /**
//     * 分配产品采购运费
//     * @param purFreightEnum 分配枚举
//     * @param freightCost 总运费
//     * @param planProductDOList 采购计划商品信息列表
//     * @return allotResMap( key 产品编码, value 产品运费)
//     *
//     * 按数量：按采购单的采购数量分摊，比如采购单A、B两个产品的采购数量分别是100和50，运费150，那么单个运费是1，A和B的分摊到的总费用是100和50
//     * 按金额：按采购单的采购金额分摊，比如采购单A、B两个产品的采购金额分别是500和2000，运费200，那么A和B的占比分别是0.2和0.8，分别乘以运费200，A和B的总运费是40和160，如果采购数量都是10，那么分摊到的单个费用是4和16
//     * 不分配：则填写的运费和其他费不会计算到产品的库存成本中，即库存明细的单位费用
//     */
//    public static Map<String, BigDecimal> allotFreight(FreightEnum purFreightEnum, BigDecimal freightCost, List<PurchasePlanProductDO> planProductDOList){
//        Map<String, BigDecimal> allotResMap = new HashMap<>();
//
//        // 校验商品列表
//        if (CollectionUtils.isEmpty(planProductDOList)){
//            throw new ServiceException(PURCHASE_PLAN_PRODUCT_EMPTY);
//        }
//        List<String> productCodeList = planProductDOList.stream().map(PurchasePlanProductDO::getProductCode).toList();
//        switch (purFreightEnum){
//            case ALLOT_BY_NUMBER:// 按数量分配
//                // 产品总数量
//                Integer totalQuantity = planProductDOList.stream().mapToInt(PurchasePlanProductDO::getQuantity).sum();
//                // 单个产品每件运费
//                BigDecimal singleFreight = NumberUtil.div(freightCost, BigDecimal.valueOf(totalQuantity)).setScale(6, BigDecimal.ROUND_HALF_UP);
//                Map<String, Integer> productInfoByQuantity = planProductDOList.stream().collect(Collectors.toMap(PurchasePlanProductDO::getProductCode, PurchasePlanProductDO::getQuantity));
//                productCodeList.stream().forEach(x->{
//                    Integer quantity = productInfoByQuantity.get(x);
//                    // 单个产品运费和
//                    BigDecimal singleProductFreight = NumberUtil.mul(singleFreight,BigDecimal.valueOf(quantity));
//                    allotResMap.put(x,singleProductFreight);
//                });
//                break;
//            case ALLOT_BY_AMOUNT:// 按金额
//                Double totalAmount = planProductDOList.stream().mapToDouble(x->x.getAmount().doubleValue()).sum();
//                Map<String, BigDecimal> productInfoByAmount = planProductDOList.stream().collect(Collectors.toMap(PurchasePlanProductDO::getProductCode, PurchasePlanProductDO::getAmount));
//                productCodeList.stream().forEach(x->{
//                    BigDecimal amount = productInfoByAmount.get(x);
//                    // 单个产品金额占比
//                    BigDecimal singleRatio = NumberUtil.div(amount, BigDecimal.valueOf(totalAmount)).setScale(6, BigDecimal.ROUND_HALF_UP);
//                    // 单个产品运费和
//                    BigDecimal singleProductFreight = NumberUtil.mul(singleRatio,freightCost);
//                    allotResMap.put(x,singleProductFreight);
//                });
//                break;
//            default:// 不分配
//                productCodeList.stream().forEach(x->allotResMap.put(x,BigDecimal.ZERO));
//                break;
//        }
//        BigDecimal checkFreight = BigDecimal.valueOf(allotResMap.values().stream().mapToDouble(BigDecimal::doubleValue).sum());
//        if (checkFreight.doubleValue() != freightCost.doubleValue() && purFreightEnum.getAllotType().intValue()!=ALLOT_BY_NONE.getAllotType().intValue()){
//            // 补齐差额运费,不可均分时补齐在最后一个产品中
//            String lastProductCode = productCodeList.get(productCodeList.size() - 1);
//            BigDecimal lastProductFreight = allotResMap.get(lastProductCode);
//            BigDecimal diffFreight = NumberUtil.sub(freightCost,checkFreight);
//            lastProductFreight = NumberUtil.add(lastProductFreight,diffFreight);
//            allotResMap.put(lastProductCode,lastProductFreight);
//        }
//        return allotResMap;
//    }
//
//    @Test
//    public void testNumber() {
//        // PurFreightEnum purFreightEnum = PurFreightEnum.BY_NONE;
//        FreightEnum purFreightEnum = FreightEnum.ALLOT_BY_NUMBER;
//        List<PurchasePlanProductDO> planProductDOList = new ArrayList<>();
//        planProductDOList.add(PurchasePlanProductDO.builder().build().setProductCode("1").setQuantity(100).setAmount(BigDecimal.valueOf(234L)));
//        planProductDOList.add(PurchasePlanProductDO.builder().build().setProductCode("2").setQuantity(100).setAmount(BigDecimal.valueOf(134L)));
//        planProductDOList.add(PurchasePlanProductDO.builder().build().setProductCode("3").setQuantity(100).setAmount(BigDecimal.valueOf(334L)));
//        BigDecimal freightCost = BigDecimal.valueOf(100);
//        Map<String, BigDecimal> allotMap = AllotUtil.allotFreight(purFreightEnum, freightCost, planProductDOList);
//        System.out.println(allotMap);
//    }
//
//    @Test
//    public void testAmount() {
//         FreightEnum purFreightEnum = FreightEnum.ALLOT_BY_AMOUNT;
//        List<PurchasePlanProductDO> planProductDOList = new ArrayList<>();
//        planProductDOList.add(PurchasePlanProductDO.builder().build().setProductCode("1").setQuantity(10).setAmount(BigDecimal.valueOf(100L)));
//        planProductDOList.add(PurchasePlanProductDO.builder().build().setProductCode("2").setQuantity(20).setAmount(BigDecimal.valueOf(100L)));
//        planProductDOList.add(PurchasePlanProductDO.builder().build().setProductCode("3").setQuantity(30).setAmount(BigDecimal.valueOf(100L)));
//        BigDecimal freightCost = BigDecimal.valueOf(100);
//        Map<String, BigDecimal> allotMap = AllotUtil.allotFreight(purFreightEnum, freightCost, planProductDOList);
//        System.out.println(allotMap);
//    }
//
//
//    /**
//     * 分摊头程费用
//     * @param volumeParam 材积参数
//     * @param freightCost 物流费用
//     * @param otherCost 其他费用
//     * @param planProductDOList 采购计划商品信息列表
//     * @return
//     *
//     * 发货单:SP180101001
//     * 发货商品A，数量:1000，A商品包装规格:20cmx30cmx40cm，单品毛重500g(0.5kg)，税费200
//     * 发货商品B，数量:2000，B商品包装规格:10cmx20cmx20cm，单品毛重1600g(1.6kg)，税费400
//     * 物流方式:顺丰快递空运(材积参数5000)，物流费用:450，其他费用:50
//     * 计算:
//     *  商品A的单个计费重=max(20x30x40/5000,0.5)=4.8
//     *  商品B的单个计费重=max(10x20x20/5000.1.6)=1.6
//     *  A商品单个头程成本:【(4.8x1000)/(4.8x1000+1.6x2000)x500+200】/1000=0.5，A商品的头程成本:0.5x1000=500
//     *  B商品单个头程成本:【B商品计费重(1.6x2000)/整票货的计费重(4.8x1000+1.6x2000)x整票货的运费500+B商品税费400】1发货量2000=0.3，B商品的头程成本:0.3x2000=600
//     */
//    public static Map<String, BigDecimal> allotHeadCost(Integer volumeParam, BigDecimal freightCost,
//                                                        BigDecimal otherCost, List<PurchasePlanProductDO> planProductDOList){
//        // 校验商品列表
//        if (CollectionUtils.isEmpty(planProductDOList)){
//            throw new ServiceException(PURCHASE_PLAN_PRODUCT_EMPTY);
//        }
//        // 计算产品计费重
//        Map<String, Double> calcRatioMap = planProductDOList.stream().collect(Collectors.toMap(PurchasePlanProductDO::getProductCode, x -> {
//            BigDecimal productVolume = NumberUtil.mul(x.getLength(), x.getWidth(), x.getHeight());
//            double volumeRatio = NumberUtil.div(productVolume, volumeParam).doubleValue();
//            double weightRatio = NumberUtil.div(x.getWeightWithGross().doubleValue(), 1000.00);
//            return volumeRatio > weightRatio ? volumeRatio : weightRatio;
//        }));
//        // 产品数量 map
//        Map<String, Integer> quantityMap = planProductDOList.stream().collect(Collectors.toMap(PurchasePlanProductDO::getProductCode, PurchasePlanProductDO::getQuantity));
//        // 计算整票货计费重
//        double calcWeight = calcRatioMap.entrySet().stream().mapToDouble(x -> {
//            Integer quantity = quantityMap.get(x.getKey());
//            return NumberUtil.mul(BigDecimal.valueOf(x.getValue()), BigDecimal.valueOf(quantity)).doubleValue();
//        }).sum();
//        // 计算产品头程成本
//        BigDecimal totalCost = NumberUtil.add(freightCost, otherCost);
//        Map<String, BigDecimal> allotResMap = planProductDOList.stream().collect(Collectors.toMap(PurchasePlanProductDO::getProductCode,x->{
//            BigDecimal mulled = NumberUtil.mul(calcRatioMap.get(x.getProductCode()), x.getQuantity(),totalCost);
//            BigDecimal divved = NumberUtil.div(mulled.doubleValue(), BigDecimal.valueOf(calcWeight));
//            BigDecimal productCost = NumberUtil.add(divved,x.getTaxAmount());
//            return productCost;
//        }));
//
//        // 分配后总金额
//        BigDecimal checkFreight = BigDecimal.valueOf(allotResMap.values().stream().mapToDouble(BigDecimal::doubleValue).sum());
//        // 总税额
//        double totalTaxAmount = planProductDOList.stream().mapToDouble(x->x.getTaxAmount().doubleValue()).sum();
//        // 物流费用+其他费用+商品税额总和
//        BigDecimal cost = NumberUtil.add(totalCost,totalTaxAmount);
//        if (checkFreight.doubleValue() != cost.doubleValue()){
//            // 补齐差额运费,不可均分时补齐在最后一个产品中
//            List<String> productCodeList = planProductDOList.stream().map(PurchasePlanProductDO::getProductCode).toList();
//            String lastProductCode = productCodeList.get(productCodeList.size() - 1);
//            BigDecimal lastProductFreight = allotResMap.get(lastProductCode);
//            BigDecimal diffFreight = NumberUtil.sub(totalCost,checkFreight);
//            lastProductFreight = NumberUtil.add(lastProductFreight,diffFreight);
//            allotResMap.put(lastProductCode,lastProductFreight);
//        }
//        return allotResMap;
//    }
//
//    @Test
//    public void testAllotHeadCost(){
//        Integer volumeParam = 5000;
//        BigDecimal freightCost = BigDecimal.valueOf(450);
//        BigDecimal otherCost = BigDecimal.valueOf(50);
//        List<PurchasePlanProductDO> planProductDOList = new ArrayList<>();
//        planProductDOList.add(PurchasePlanProductDO.builder().build().setProductCode("1").setLength(20).setWidth(30).setHeight(40).setQuantity(1000).setTaxAmount(BigDecimal.valueOf(200)).setWeightWithGross(500).setAmount(BigDecimal.valueOf(100L)));
//        planProductDOList.add(PurchasePlanProductDO.builder().build().setProductCode("2").setLength(10).setWidth(20).setHeight(20).setQuantity(2000).setTaxAmount(BigDecimal.valueOf(400)).setWeightWithGross(1600).setAmount(BigDecimal.valueOf(100L)));
//        Map<String, BigDecimal> stringBigDecimalMap = AllotUtil.allotHeadCost(volumeParam, freightCost, otherCost, planProductDOList);
//        System.out.println(stringBigDecimalMap);
//    }
//}
