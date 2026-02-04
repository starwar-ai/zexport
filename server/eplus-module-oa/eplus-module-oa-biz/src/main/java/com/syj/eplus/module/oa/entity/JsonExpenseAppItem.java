package com.syj.eplus.module.oa.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class JsonExpenseAppItem {

    /**
     * 餐饮费
     */
    private BigDecimal foodExpense;

    /**
     * 水果食品费
     */
    private BigDecimal fruitExpense;

    /**
     * 礼品费
     */
    private BigDecimal giftExpense;

    /**
     * 交通费
     */
    private BigDecimal trafficExpense;

    /**
     * 其他费用
     */
    private BigDecimal otherExpense;

    /**
     * 其他费用说明
     */
    private String otherExpenseDesc;

    /**
     * 总费用
     */
    private BigDecimal totalExpense;
}
