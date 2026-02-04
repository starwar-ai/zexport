package com.syj.eplus.module.wms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 来源单据类型  采购入/出库、盘点入/出库、调拨入/出库、组套件入/出库
 */
@Getter
@AllArgsConstructor
public enum StockSourceTypeEnum {

    PURCHASE(1,"采购"),
    STOCK_SURPLUS(2,"盘盈"),
    STOCK_LOSS(3,"盘亏"),
    ALLOCATION(4,"调拨"),
    ASSEMBLE(5,"组套件"),
    MANUSACTURE(6,"加工单"),
    IMPORT(7,"库存导入");
    private final Integer value;

    private final String desc;
}
