package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description：佣金类型枚举
 * @Author：du
 * @Date：2024/6/19 10:59
 */
@Getter
@AllArgsConstructor
public enum CommissionRateEnum {
    // 按比例
    RATE(2, "比例"),
    //新增
    FEE(1, "金额");

    private Integer type;

    private String name;
}
