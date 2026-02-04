package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PurchaseTypeEnum {
    INVENTORY (1, "库存采购"),
    STANDARD(2, "标准采购"),
    FITTINGS(3, "配件辅料采购"),
    SAMPLE(4, "样品采购");


    private final Integer code;

    private final String name;

    public static PurchaseTypeEnum getByValue(int value) {
        for (PurchaseTypeEnum status : PurchaseTypeEnum.values()) {
            if (status.code == value) {
                return status;
            }
        }
        return null; // 或者抛出异常
    }


}
