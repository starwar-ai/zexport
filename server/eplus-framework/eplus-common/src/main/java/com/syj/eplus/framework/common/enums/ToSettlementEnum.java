package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ToSettlementEnum {

    NOT_YET_TRANSFERRED(1, "未转"),
    PARTIAL_CONVERSION(2, "部分转"),
    TRANSFERRED(3, "已转");

    private final Integer status;

    private final String name;
}
