package com.syj.eplus.module.crm.enums.cust;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description：客户阶段枚举
 * @Author：du
 * @Date：2024/1/11 14:33
 */
@Getter
@AllArgsConstructor
public enum CustomerStageEnum {
    /**
     * 线索客户 Lead customers
     */
    LEAD_CUSTOMERS(1),
    /**
     * 普通客户 Ordinary customers
     */
    ORDINARY_CUSTOMERS(2),
    /**
     * 重要客户 Key customers
     */
    KEY_CUSTOMERS(3),
    /**
     * 失效客户 Lapsed Customers
     */
    LAPSED_CUSTOMERS(4);

    private final Integer value;
}
