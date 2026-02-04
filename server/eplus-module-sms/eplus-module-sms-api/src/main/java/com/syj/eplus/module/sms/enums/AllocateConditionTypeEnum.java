package com.syj.eplus.module.sms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 利润率分配条件
 */
@Getter
@AllArgsConstructor
public enum AllocateConditionTypeEnum {

    NONE(1,"无"),
    GT(2,">"),
    GE(3,">="),
    LT(4,"<"),
    LE(5,"<="),
    ;

    private final Integer value;

    private final String desc;
}
