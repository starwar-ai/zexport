package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FmsOaPaymentRelationTypeEnum {
    PURCHASE(1, "采购合同"),
    EMSSEND(2, "寄件");
    private final Integer code;

    private final String name;


}
