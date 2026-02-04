package com.syj.eplus.framework.common.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ClaimStatusEnum {
    NOT_CLAIM(0, "未认领"),
    PART_CLAIM(1, "部分认领"),
    CLAIM(2, "已认领");
    private final Integer status;

    private final String name;
}
