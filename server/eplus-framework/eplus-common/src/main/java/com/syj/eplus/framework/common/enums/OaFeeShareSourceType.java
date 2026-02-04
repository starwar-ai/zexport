package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum OaFeeShareSourceType {
    CUST(1,"客户"),
    VENDEOR(2,"供应商"),
    SMS(3,"销售合同"),
    PURCHASE(4,"采购合同"),
    DEPT(5,"部门"),
    USER(6,"员工");
    private final Integer Code;

    private final String name;
}
