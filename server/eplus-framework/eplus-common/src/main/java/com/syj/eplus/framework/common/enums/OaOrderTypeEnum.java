package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum OaOrderTypeEnum {

    SMS(1,"销售合同"),
    PURCHASE(2,"采购合同"),
    OTHER(3,"无订单");
    private final Integer Code;

    private final String name;
}
