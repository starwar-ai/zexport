package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum QmsHandleStateEnum {
    REWORK(1, "返工重验"),
    RELEASE(2, "让步放行");

    private Integer value;

    private String name;
}
