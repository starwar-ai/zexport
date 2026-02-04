package com.syj.eplus.framework.common.entity;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PaymentAppDTO {

    private String code;

    /**
     * 已支付金额
     */
    private JsonAmount paymentAmount;
    /**
     * 支付状态
     */
    private Integer paymentStatus;
    /**
     * 支付日期
     */
    private LocalDateTime paymentDate;
    /**
     * 支付账号
     */
    private String bankAccount;
    /**
     * 银行
     */
    private String bank;

    /**
     * 出纳员
     */
    private UserDept cashier;

    /**
     * 附件
     */
    private List<SimpleFile> annex;

}
