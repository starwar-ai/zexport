package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum OaMarketRelationTypeEnum {

    USER(1,"人力费用"),
    OA(2,"财务费用"),
    ADMINISTRATION(3,"行政费用");
    private final Integer Code;

    private final String name;
}
