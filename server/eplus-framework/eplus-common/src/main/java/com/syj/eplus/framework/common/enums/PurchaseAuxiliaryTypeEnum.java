package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PurchaseAuxiliaryTypeEnum {
    ORDER (1, "订单相关"),
    PRODUCT(2, "产品相关"),
    OTHER(3, "其他");


    private final Integer code;

    private final String name;


}
