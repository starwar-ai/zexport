package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum OaFeeShareTypeEnum {

    NOORDER(1,"客户/供应商/部门"),
    OPERATION(2,"公司运营"),
    MARKET(3,"市场拓展"),
    PROJECT(4,"项目研发");
    private final Integer Code;

    private final String name;
}
