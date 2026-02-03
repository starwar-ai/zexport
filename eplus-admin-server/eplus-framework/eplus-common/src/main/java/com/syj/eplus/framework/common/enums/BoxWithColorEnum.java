package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum BoxWithColorEnum {
    YELLOW(1, "黄色"),
    TRANSPARENT(2, "透明"),
    WHITE(3, "白色纸质");

    private Integer value;

    private String name;
}
