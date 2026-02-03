package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StageTypeEnum {

    /**
     * 潜在
     */
    CLUE(1,"潜在"),
    /**
     * 潜在
     */
    FORMAL(2,"正式");

    private final Integer value;
    private final String name;
}
