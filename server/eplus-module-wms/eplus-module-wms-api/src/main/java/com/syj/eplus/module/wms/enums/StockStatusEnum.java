package com.syj.eplus.module.wms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 出入库状态
 */
@Getter
@AllArgsConstructor
public enum StockStatusEnum {


    NOT(1,"未收/出货"),

    PART(2,"部分收/出货"),

    ALL(3,"完全收/出货");

    private final Integer value;

    private final String desc;
}
