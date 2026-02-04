package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentPlanExeStatusEnum {

    PAID(1,"已支付"),
    IN_PAID(2,"支付中"),
    UNPAID(0,"未支付"),
    PART_PAID(3,"部分支付");

    private Integer value;

    private String name;
}
