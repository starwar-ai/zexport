package com.syj.eplus.module.wms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 单据状态 1-未确认 2-已确认 3-作废
 */
@Getter
@AllArgsConstructor
public enum StockBillStatusEnum {


    UN_CONFIRM(1,"未确认"),
    CONFIRMED(2,"已确认"),
    CANCEL(3,"作废");

    private final Integer value;

    private final String desc;
}
