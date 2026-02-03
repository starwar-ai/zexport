package com.syj.eplus.module.wms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 出入库类型
 */
@Getter
@AllArgsConstructor
public enum StockTypeEnum {


    IN_STOCK(1,"入库"),

    OUT_STOCK(2,"出库");

    private final Integer value;

    private final String desc;
}
