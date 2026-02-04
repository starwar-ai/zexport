package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InspectionNodeEnum {
    INTERIM(1, "中期验货"),
    FINAL(2, "最终验货");
    private final Integer code;

    private final String name;


}

