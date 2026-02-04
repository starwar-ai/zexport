package com.syj.eplus.framework.common.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class JsonSpecificationEntity {
    /**
     * 外箱规格长度
     */
    private BigDecimal outerboxLength;
    /**
     * 外箱规格宽度
     */
    private BigDecimal outerboxWidth;
    /**
     * 外箱规格高度
     */
    private BigDecimal outerboxHeight;
    /**
     * 外箱体积
     */
    private BigDecimal outerboxVolume;

    /**
     * 外箱毛重
     */
    private JsonWeight outerboxGrossweight;

    /**
     * 外箱净重
     */
    private JsonWeight outerboxNetweight;
//
//    /**
//     * 外箱规格单位
//     */
//    private Integer outerboxUnit;

}
