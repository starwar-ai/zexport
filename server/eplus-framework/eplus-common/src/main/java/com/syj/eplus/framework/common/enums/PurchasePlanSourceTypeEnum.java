package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PurchasePlanSourceTypeEnum {
    CREATE (1, "手工创建"),
    SALE_CONTRACT(2, "销售合同下推"),
    PURCHASE_CONTRACT(3, "采购合同下推");


    private final Integer code;

    private final String name;


}
