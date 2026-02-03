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
public enum ShippingPlanStatusEnum {
    PAD_SUBMIT(0, "待提交"),
    AWAITING_PROCESSING(1, "待处理"),
    COMPLETED(2, "已完成"),
    CASE_CLOSED(3, "已结案");

    private Integer value;

    private String name;
}
