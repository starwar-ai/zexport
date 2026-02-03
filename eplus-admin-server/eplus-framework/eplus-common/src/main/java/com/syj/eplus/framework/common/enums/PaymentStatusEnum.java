package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentStatusEnum {

    PAID(1,"已支付"),
    UNPAID(0,"未支付"),
    IN_PAYMENT(2,"支付中"),
    PART_PAID(3,"部分支付");

    private Integer value;

    private String name;
}
