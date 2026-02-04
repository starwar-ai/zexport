package com.syj.eplus.module.wms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 库存锁定-原单据类型  1-销售合同 2-加工单
 */
@Getter
@AllArgsConstructor
public enum StockLockSourceTypeEnum {


    SALE_CONTRACT(1,"销售合同"),
    PROCESSING(2,"加工单"),
    TRANSFER_ORDER(3,"调拨单"),
    PURCHASE_PLAN(4,"采购计划"),
    OUT_STOCK(5,"出库通知单");

    private final Integer value;

    private final String desc;
}
