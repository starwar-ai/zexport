package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ConvertedFlagEnum {
    NOT_CONVERTED(0, "未转"),
    PART_CONVERTED(2, "部分转"),
    CONVERTED(1, "已转");

    private Integer status;

    private String name;
}
