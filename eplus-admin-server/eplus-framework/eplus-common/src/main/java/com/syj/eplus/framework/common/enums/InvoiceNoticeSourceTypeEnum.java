package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InvoiceNoticeSourceTypeEnum {
    SHIPMENT(1, "出运"),
    PURCHASE(2, "采购");
    private final Integer value;
    private final String name;
}
