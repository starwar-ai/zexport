package com.syj.eplus.framework.common.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/09/16:46
 * @Description:
 */
@Data
public class PaymentApplyEntity {
    /**
     * 付款申请单主键
     */
    private Long paymentApplyId;
    /**
     * 付款申请编号
     */
    private String paymentApplyCode;

    /**
     * 付款单位
     */
    private String companyName;

    /**
     * 申请日期
     */
    private LocalDateTime applyDate;

    /**
     * 计划付款日
     */
    private LocalDateTime planPaymentDate;

    /**
     * 申请付款日
     */
    private LocalDateTime applyPaymentDate;

    /**
     * 申请金额
     */
    private JsonAmount applyAmount;

    /**
     * 付款类型
     */
    private Integer paymentType;

    /**
     * 关联采购合同
     */
    private String purchaseContractCode;

    /**
     * 关联销售合同
     */
    private String saleContractCode;

    /**
     * 付款对象名称
     */
    private String businessObjectName;

    /**
     * 申请人
     */
    private UserDept applyer;
    /**
     * 状态
     */
    private Integer status;

}
