package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/16 17:37
 */
@Getter
@AllArgsConstructor
public enum SaleTypeEnum {
    DOMESTIC_SALE_CONTRACT(2, "内销合同"),

    EXPORT_SALE_CONTRACT(1, "外销合同"),
    FACTORY_SALE_CONTRACT(3, "外币采购合同");

    private Integer value;

    private String name;

    public static SaleTypeEnum getByValue(int value) {
        for (SaleTypeEnum status : SaleTypeEnum.values()) {
            if (status.value == value) {
                return status;
            }
        }
        return null; // 或者抛出异常
    }
}
