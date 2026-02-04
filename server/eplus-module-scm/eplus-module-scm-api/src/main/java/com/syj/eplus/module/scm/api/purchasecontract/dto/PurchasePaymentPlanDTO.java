package com.syj.eplus.module.scm.api.purchasecontract.dto;

import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonPayment;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PurchasePaymentPlanDTO {

    /**
     * 主键
     */
    private Long id;
    /**
     * 合同编号
     */
    private String contractCode;
    /**
     * 步骤
     */
    private Integer step;
    /**
     * 支付方式
     */
    private Integer paymentMethod;
    /**
     * 起始点
     */
    private Integer dateType;
    /**
     * 起始日
     */
    private LocalDateTime startDate;
    /**
     * 天数
     */
    private Integer days;
    /**
     * 预计付款日
     */
    private LocalDateTime expectedReceiptDate;
    /**
     * 付款比例
     */
    private BigDecimal paymentRatio;
    /**
     * 应付金额
     */
    private JsonAmount receivableAmount;
    /**
     * 实付金额
     */
    private JsonAmount receivedAmount;

    /**
     * 已申请金额
     */
    private JsonAmount appliedAmount;
    /**
     * 是否控制采购
     */
    private Integer controlPurchaseFlag;
    /**
     * 状态
     */
    private Integer exeStatus;

    private Integer changeFlag;

    private String sourceCode;

    private List<JsonEffectRange> effectRangeList;

    /**
     * 本次申请金额
     */
    private BigDecimal applyAmount;

    /**
     * 实际付款比例
     */
    private BigDecimal realPaymentRatio;

    /**
     * 付款时间
     */
    private LocalDateTime paymentTime;

    /**
     * 付款信息
     */
    private List<JsonPayment> paymentMsg;
}
