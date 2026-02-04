package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RepaySourceTypeEnum {
    REPAY_APP(1,"还款单"),

    TRAVEL_REIMB(2,"出差报销"),

    ENTERTAIN_REIMB(3,"招待报销"),
    OTHER_REIMB(4,"其他费用报销"),
    GENERAL_REIMB(5,"通用费用报销");

    private Integer type;

    private String name;
}
