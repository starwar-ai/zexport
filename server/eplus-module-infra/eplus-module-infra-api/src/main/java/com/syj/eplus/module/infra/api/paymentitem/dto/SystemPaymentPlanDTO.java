package com.syj.eplus.module.infra.api.paymentitem.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 系统付款计划 DTO
 *
 * @Author: du
 * @Date: 2024/08/05/20:25
 */
@Data
public class SystemPaymentPlanDTO {
    /**
     * 主键
     */
    private Long id;
    /**
     * 收款方式主键
     */
    private Long paymentId;
    /**
     * 支付方式
     */
    private Integer paymentMethod;
    /**
     * 步骤
     */
    private Integer step;
    /**
     * 起始点
     */
    private Integer dateType;
    /**
     * 天数
     */
    private Integer days;
    /**
     * 付款比例
     */
    private BigDecimal paymentRatio;
    /**
     * 是否控制采购
     */
    private Integer controlPurchaseFlag;
    /**
     * 是否控制发票
     */
    private Integer controlInvoiceFlag;
    /**
     * 状态
     */
    private Integer exeStatus;
}
