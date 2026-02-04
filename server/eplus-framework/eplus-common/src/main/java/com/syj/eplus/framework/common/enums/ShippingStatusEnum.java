package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/15 15:40
 */
@Getter
@AllArgsConstructor
public enum ShippingStatusEnum {
    AWAITING_PROCESSING(1, "待处理"),
    READY_FOR_SHIPMENT(2, "待出运"),
    SHIPPED_IN_BATCH(3, "分批出运"),
    SHIPPED(4, "已出运"),
    COMPLETED(5, "已完成"),
    CASE_CLOSED(6, "已结案");

    private Integer value;

    private String name;
}
