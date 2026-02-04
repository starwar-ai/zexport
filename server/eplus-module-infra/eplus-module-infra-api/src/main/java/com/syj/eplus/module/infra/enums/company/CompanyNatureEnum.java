package com.syj.eplus.module.infra.enums.company;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 企业性质
 */
@Getter
@AllArgsConstructor
public enum CompanyNatureEnum {

    FACTORY(1,"工厂"),

    FOREIGN_TRADE(2,"外贸"),

    DOMESTIC_TRADE(3,"内销"),

    INTERNAL_CUST(4,"内部客户"),
    ;

    private final Integer value;

    private final String desc;
}
