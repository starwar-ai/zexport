package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/16 15:45
 */
@Getter
@AllArgsConstructor
public enum ShettlementDateTypeEnum {
    SHIPMENT_DATE(3, "出运日"),

    OUTSTOCK_DATE(2, "出库日"),

    SIGNBACK_DATE(1, "回签日");

    private Integer value;

    private String name;
}
