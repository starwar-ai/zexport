package com.syj.eplus.module.fms.controller.admin.custclaim.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/24 11:27
 */
@Data
public class CustClaimResp {
    private Long id;
    /**
     * 公司抬头
     */
    private String companyTitle;
    /**
     * 入账单位id
     */
    private Long companyId;
    /**
     * 入账单位名称
     */
    private String companyName;
    /**
     * 银行
     */
    private String bank;
    /**
     * 入账币别
     */
    private String currency;
    /**
     * 入账金额
     */
    private BigDecimal amount;
    /**
     * 已认领金额
     */
    private BigDecimal claimedAmount;
    /**
     * 待认领金额
     */
    private BigDecimal unclaimedAmount;
}
