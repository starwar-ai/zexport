package com.syj.eplus.module.wms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Desc——盘点结果 1-盘盈 2-盘亏 3-盘平
 * Create by Rangers at  2024-06-26 14:42
 */
@Getter
@AllArgsConstructor
public enum StocktakeResultEnum {

    SURPLUS(1,"盘盈"),

    DEFICIT(2,"盘亏"),

    BE_EQUAL(3,"盘平");

    private final Integer value;

    private final String desc;
}
