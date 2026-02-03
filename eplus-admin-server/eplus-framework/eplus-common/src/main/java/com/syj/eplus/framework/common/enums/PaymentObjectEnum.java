package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentObjectEnum {

    CUST(3,"客户"),
    VENDER(2,"行政供应商"),
    VENDER_BIZ(1,"业务供应商");

    private final Integer code;

    private final String name;

    public static String getNameByCode(Integer code) {
        for (PaymentObjectEnum paymentObjectEnum : PaymentObjectEnum.values()) {
            if (paymentObjectEnum.getCode().equals(code)) {
                return paymentObjectEnum.getName();
            }
        }
        return null;
    }
}
