package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentAppTypeEnum {
    PRE_PAID(1,"预付"),
    PAYMENT(2,"付款"),
    OFF_SET(3,"冲账");

    private Integer type;

    private String name;
}
