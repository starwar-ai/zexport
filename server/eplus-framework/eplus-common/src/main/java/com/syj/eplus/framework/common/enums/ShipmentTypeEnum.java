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
public enum ShipmentTypeEnum {
    SHIPMENT_DATE(1, "整柜"),

    SIGN_BACK_DATE(2, "散货");

    private Integer value;

    private String name;
}
