package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExecuteStatusEnum {
    NOT_EXECUTED(0,"未执行"),
    EXECUTED(1,"已执行"),
    PART_EXECUTED(2,"部分执行");
    private final Integer status;

    private final String name;
}
