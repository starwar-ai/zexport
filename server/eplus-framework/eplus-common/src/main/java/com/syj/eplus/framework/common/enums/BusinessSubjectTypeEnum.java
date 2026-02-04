package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BusinessSubjectTypeEnum {
    // 客户
    CUST(3,"客户"),
    // 供应商
    VENDER(2,"行政供应商"),
    VENDER_BIZ(1,"业务供应商"),
    // 出差报销
    TRAVEL_REMIB(4, "个人");


    private final Integer code;

    private final String name;

    public static String getNameByCode(Integer code) {
        for (BusinessSubjectTypeEnum businessSubjectTypeEnum : BusinessSubjectTypeEnum.values()) {
            if (businessSubjectTypeEnum.getCode().equals(code)) {
                return businessSubjectTypeEnum.getName();
            }
        }
        return null;
    }
}
