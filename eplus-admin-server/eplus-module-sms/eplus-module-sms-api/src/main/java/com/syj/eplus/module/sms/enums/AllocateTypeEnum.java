package com.syj.eplus.module.sms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 利润率分配类型
 */
@Getter
@AllArgsConstructor
public enum AllocateTypeEnum {

    NONE(1,"不分配"),

    FIX_RATIO(2,"固定利润率"),

    RANDOM_RATIO(3,"随机利润率"),
    ;

    private final Integer value;

    private final String desc;


}
