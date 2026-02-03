package com.syj.eplus.framework.common.util;

import cn.hutool.core.collection.CollUtil;
import com.syj.eplus.framework.common.dict.CalculationDict;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.JsonWeight;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * 计算规格工具类
 */
public class CalcSpecificationUtil {

    /**
     * 计算规格总体积
     * @param specificationEntityList 规格列表
     * @return 规格总体积
     */
    public static BigDecimal calcSpecificationTotalVolume(List<JsonSpecificationEntity> specificationEntityList){
        if (CollUtil.isEmpty(specificationEntityList)){
            return BigDecimal.ZERO;
        }
        return specificationEntityList.stream().map(JsonSpecificationEntity::getOuterboxVolume).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    /**
     * 计算规格总毛重
     * @param specificationEntityList 规格列表
     * @return 规格总毛重
     */
    public static JsonWeight calcSpecificationTotalGrossweight(List<JsonSpecificationEntity> specificationEntityList){
        if (CollUtil.isEmpty(specificationEntityList)){
            return new JsonWeight().setWeight(BigDecimal.ZERO).setUnit(CalculationDict.GRAM);
        }
        return specificationEntityList.stream().map(JsonSpecificationEntity::getOuterboxGrossweight).reduce(JsonWeight::add).orElse(new JsonWeight().setWeight(BigDecimal.ZERO).setUnit(CalculationDict.GRAM));
    }

    /**
     * 计算规格总净重
     * @param specificationEntityList 规格列表
     * @return 规格总净重
     */
    public static JsonWeight calcSpecificationTotalNetweight(List<JsonSpecificationEntity> specificationEntityList){
        if (CollUtil.isEmpty(specificationEntityList)){
            return new JsonWeight().setWeight(BigDecimal.ZERO).setUnit(CalculationDict.GRAM);
        }
        return specificationEntityList.stream().map(JsonSpecificationEntity::getOuterboxNetweight).reduce(JsonWeight::add).orElse(new JsonWeight().setWeight(BigDecimal.ZERO).setUnit(CalculationDict.GRAM));
    }

    /**
     * 计算总体积
     * @param specificationEntityList 规格列表
     * @param boxCount 箱数
     * @return 总体积
     */
    public static BigDecimal calcTotalVolumeByBoxCount(List<JsonSpecificationEntity> specificationEntityList,BigDecimal boxCount){
        if (CollUtil.isEmpty(specificationEntityList)||Objects.isNull(boxCount)||BigDecimal.ZERO.compareTo(boxCount) == 0){
            return BigDecimal.ZERO;
        }
        return specificationEntityList.stream().map(s -> NumUtil.mul(s.getOuterboxVolume(),boxCount)).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    /**
     * 根据箱数计算总毛重
     * @param specificationEntityList 规格列表
     * @param boxCount 箱数
     * @return 总毛重
     */
    public static JsonWeight calcTotalGrossweightByBoxCount(List<JsonSpecificationEntity> specificationEntityList,BigDecimal boxCount){
        if (CollUtil.isEmpty(specificationEntityList)||Objects.isNull(boxCount)||BigDecimal.ZERO.compareTo(boxCount) == 0){
            return new JsonWeight().setUnit(CalculationDict.GRAM).setWeight(BigDecimal.ZERO);
        }
        JsonWeight grossWeight = calcSpecificationTotalGrossweight(specificationEntityList);
        // 返回新对象，避免修改原始数据
        return new JsonWeight().setWeight(NumUtil.mul(grossWeight.getWeight(),boxCount)).setUnit(grossWeight.getUnit());
    }

    /**
     * 根据箱数计算总净重
     * @param specificationEntityList 规格列表
     * @param boxCount 箱数
     * @return 总净重
     */
    public static JsonWeight calcTotalNetWeightByBoxCount(List<JsonSpecificationEntity> specificationEntityList,BigDecimal boxCount){
        if (CollUtil.isEmpty(specificationEntityList)||Objects.isNull(boxCount)||BigDecimal.ZERO.compareTo(boxCount) == 0){
            return new JsonWeight().setUnit(CalculationDict.GRAM).setWeight(BigDecimal.ZERO);
        }
        JsonWeight netweight = calcSpecificationTotalNetweight(specificationEntityList);
        // 返回新对象，避免修改原始数据
        return new JsonWeight().setWeight(NumUtil.mul(netweight.getWeight(),boxCount)).setUnit(netweight.getUnit());
    }
}
