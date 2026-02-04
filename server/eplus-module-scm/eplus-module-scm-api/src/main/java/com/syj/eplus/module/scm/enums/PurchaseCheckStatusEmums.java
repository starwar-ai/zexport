package com.syj.eplus.module.scm.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 采购哦合同验货状态：0-未验货、1-部分通过、2-全部通过
 */
@Getter
@AllArgsConstructor
public enum PurchaseCheckStatusEmums {

    NOT_CHECK(1,"未验货"),
    PART_CHECK(2,"部分通过"),
    ALL_PASS(3,"全部通过"),
    ;

    private final Integer value;

    private final String desc;
}
