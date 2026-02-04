package com.syj.eplus.framework.common.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentMarkTypeEnum {
    NOT_PRODUCT_PAY(1,"未产先付"),
    PRODUCT_PAY(2,"已产先付"),
    OUT_PAY(3,"已出先付"),
    NORMAL_PAY(4,"正常付款");

    private Integer status;

    private String name;
}
