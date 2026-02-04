package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PurchaseCheckEnum {
    NOT_CHECK(1, "未验货"),
    PART_CHECK(2, "部分验货"),
    CHECKED(3, "已验货");

    private final Integer status;

    private final String name;

}
