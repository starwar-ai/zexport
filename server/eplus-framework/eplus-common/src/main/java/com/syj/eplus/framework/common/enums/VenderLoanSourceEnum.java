package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VenderLoanSourceEnum {


    PERSON(1, "个人"),


    VENDER(2, "供应商");

    private Integer value;

    private String name;
}
