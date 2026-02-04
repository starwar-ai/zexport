package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum PayFeeStatusEnum {
    NOT_APPLY(0,"未申请"),
    APPLY(1,"已申请");

    private Integer value;

    private String name;
}
