package com.syj.eplus.module.scm.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Desc——采购运费分配枚举
 * Create by Rangers at  2024-04-12 10:31
 */
@Getter
@AllArgsConstructor
public enum FreightEnum {


    /**
     * 不分配
     */
    ALLOT_BY_NONE(0,"不分配"),

    /**
     * 按数量分配
     */
    ALLOT_BY_NUMBER(1,"按数量分配"),

    /**
     * 按金额分配
     */
    ALLOT_BY_AMOUNT(2,"按金额分配");


    private final Integer allotType;


    private final String allotDesc;
}
