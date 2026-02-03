package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SpecialPermissionFlagEnum {
    /**
     * 常规
     */
    NORMAL(1, "常规"),
    /**
     * 紧急
     */
    EMERGENCY(2, "紧急"),
    /**
     * 特批
     */
    SPECIAL_PERMISSION(3, "特批");

    private Integer status;

    private String name;
}
