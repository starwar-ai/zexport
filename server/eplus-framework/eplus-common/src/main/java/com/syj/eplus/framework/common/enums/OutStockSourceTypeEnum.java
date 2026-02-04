package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OutStockSourceTypeEnum {

    DEFICIT(1,"盘点单"),
    CONTAINER_TRANSPORTATION(2,"拉柜通知单"),
    OUT_NOTICE(3,"出库通知单");

    private final Integer type;

    private final String name;
}
