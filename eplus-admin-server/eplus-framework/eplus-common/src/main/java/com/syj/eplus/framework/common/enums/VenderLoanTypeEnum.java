package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VenderLoanTypeEnum {
    CASH(1, "现金"),
    TRANSFER(2, "转账"),
    RETURN(3, "退款");

    private Integer value;

    private String name;
}
