package com.syj.eplus.module.wms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 盘库状态
 */
@Getter
@AllArgsConstructor
public enum StocktakeStatusEnum {

    NOT_START(1,"未开始"),

    IN_PROGRESS(2,"进行中"),

    ENDED(3,"已结束");

    private final Integer value;

    private final String desc;
}
