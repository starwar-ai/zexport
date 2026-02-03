package com.syj.eplus.framework.common.util;

import cn.iocoder.yudao.framework.common.dict.CommonDict;
import com.syj.eplus.framework.common.dict.CalculationDict;
import com.syj.eplus.framework.common.entity.JsonWeight;

import java.math.BigDecimal;
import java.util.Objects;


public class JsonWeightUtil {

    private static final String KG = "kg";

    private static final String G = "g";

    /**
     * 将重量单位转换为KG
     * @param jsonWeight 重量
     * @return 转换后的重量
     */
    public static JsonWeight convertToKg(JsonWeight jsonWeight){
        if (Objects.isNull(jsonWeight)){
            return jsonWeight;
        }
        String unit = jsonWeight.getUnit();
        BigDecimal weight = jsonWeight.getWeight();
        if (Objects.isNull(unit)){
            return jsonWeight;
        }
        if (unit.equalsIgnoreCase(G)) {
            weight = NumUtil.div(weight, CalculationDict.ONE_THOUSAND);
        }
        return new JsonWeight().setWeight(weight).setUnit(KG);
    }

    /**
     * 将重量单位转换为G
     * @param jsonWeight 重量
     * @return 转换后的重量
     */
    public static JsonWeight convertTog(JsonWeight jsonWeight){
        if (Objects.isNull(jsonWeight)){
            return jsonWeight;
        }
        String unit = jsonWeight.getUnit();
        BigDecimal weight = jsonWeight.getWeight();
        if (Objects.isNull(unit)){
            return jsonWeight;
        }
        if (unit.equalsIgnoreCase(KG)) {
            weight = NumUtil.mul(weight, CalculationDict.ONE_THOUSAND);
        }
        return new JsonWeight().setWeight(weight).setUnit(G);
    }

    /**
     * 乘法运算
     * @param jsonWeight 待乘的量
     * @param value 乘数
     * @return 乘法结果
     */
    public static JsonWeight mul(JsonWeight jsonWeight,Integer value){
        if (Objects.isNull(jsonWeight) || Objects.isNull(value)) {
            return new JsonWeight(BigDecimal.ZERO, CommonDict.EMPTY_STR);
        }
        BigDecimal weight = jsonWeight.getWeight();
        String unit = jsonWeight.getUnit();
        if (Objects.isNull(weight)) {
            return new JsonWeight(BigDecimal.ZERO, CommonDict.EMPTY_STR);
        }
        // 进行乘法运算
        BigDecimal newWeight =NumUtil.mul(weight, value);
        return new JsonWeight().setWeight(newWeight).setUnit(unit);
    }
}
