package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum PayStatusEnum {
    NOT_APPLY(1,"未申请"),
    APPLY(2,"已申请"),
    PAYED(3,"已支付");

    private Integer value;

    private String name;
}
