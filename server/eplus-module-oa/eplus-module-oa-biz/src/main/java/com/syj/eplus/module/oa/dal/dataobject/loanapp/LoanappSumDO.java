package com.syj.eplus.module.oa.dal.dataobject.loanapp;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoanappSumDO {

    /**
     * 币种
     */
    private String currency;
    /**
     * 借款合计
     */
    private BigDecimal amountSum;
    /**
     * 支付金额合计
     */
    private BigDecimal paymentAmountSum;
    /**
     * 剩余未还款合计
     */
    private BigDecimal outAmountSum;
    /**
     * 还款金额合计
     */
    private BigDecimal repayAmountSum;
    /**
     * 还款中金额合计
     */
    private BigDecimal inRepayAmountSum;
}
