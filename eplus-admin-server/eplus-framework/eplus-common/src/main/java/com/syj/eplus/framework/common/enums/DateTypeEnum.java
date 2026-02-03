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
public enum DateTypeEnum {
    SHIPMENT_DATE(1, "出运日并登票完成"),

    SIGN_BACK_DATE(2, "回签日"),

    INSPECTION_DATE(3, "验货日"),

    INSPECTION_TICKET_DATE(4, "验货日并登票完成"),

    TICKET_DATE(5, "登票完成");

    private Integer value;

    private String name;
}
