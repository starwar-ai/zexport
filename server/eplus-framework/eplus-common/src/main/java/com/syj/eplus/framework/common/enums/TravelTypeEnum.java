package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TravelTypeEnum {
    BUSINESS_TRIP(1, "公出"),
    DOMESTIC_BUSINESS_TRIP(2, "国内出差"),
    INTERNATIONAL_BUSINESS_TRIP(3, "国外出差");

    private Integer type;

    private String name;
}
