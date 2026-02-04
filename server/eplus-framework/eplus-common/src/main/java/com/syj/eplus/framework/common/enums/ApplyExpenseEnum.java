package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApplyExpenseEnum {
    CREATE(1,"创建报销单"),
    CLOSE(2,"作废报销单");

    private Integer value;

    private String name;
}
