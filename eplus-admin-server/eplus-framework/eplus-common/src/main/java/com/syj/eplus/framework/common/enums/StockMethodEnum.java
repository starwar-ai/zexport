package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StockMethodEnum {

    /**
     * 锁定库存
     */
    LOCK(1,"锁定库存"),
    /**
     * 库存明细
     */
    STOCK(2,"库存明细");

    private final Integer type;
    private final String name;
}
