package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum OaNotOrderRelationTypeEnum {

    CUST(1,"客户"),
    VENDER(2,"供应商"),
    DEPT(3,"部门");
//    USER(4,"个人");
    private final Integer Code;

    private final String name;
}
