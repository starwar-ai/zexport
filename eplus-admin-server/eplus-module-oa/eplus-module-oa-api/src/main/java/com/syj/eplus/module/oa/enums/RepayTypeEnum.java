package com.syj.eplus.module.oa.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description：
 * @Author：chengbo
 * @Date：2024/1/11 14:37
 */
@Getter
@AllArgsConstructor
public enum RepayTypeEnum {
    /**
     * 现金
     */
    CASH(1),
    /**
     * 转账
     */
    TRANSFER(2),
    /**
     * 报销
     */
    Reimb(3);

    private final Integer Value;
}
