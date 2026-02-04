package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VenderTypeEnum {
    BUSINESS_VENDER(1, "业务供应商"),
    ADMINISTRATION_VENDER(2, "行政供应商");

    private Integer value;

    private String name;
}
