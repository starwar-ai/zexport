package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum OaPaymentAppRealtionTypeEnum {
//    PURCHASE_CONTRACT(1, "采购合同"),
    EMS_SEND(2, "寄件"),
    DMS_FORWARDER(3, "出运船代费用"),

    AUXILIARY_PURCHASE (4, "包材采购合同");

    private Integer value;

    private String name;
}
