package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ShippedAddressEnum {
    FACTORY(1, "工厂发货"),

    BILL(2, "仓库发货");


    private Integer value;

    private String name;
}
