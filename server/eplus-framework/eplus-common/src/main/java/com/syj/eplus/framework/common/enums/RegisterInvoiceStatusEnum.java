package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RegisterInvoiceStatusEnum {
    REGISTERED(1, "已登记"),
    PART_REGISTERED(2, "部分登票"),
    NOT_REGISTERED(0, "未登记");

    private Integer value;
    private String name;
}
