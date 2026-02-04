package com.syj.eplus.module.scm.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/07/16:06
 * @Description:
 */
@Data
@Schema(description = "付款申请付款计划")
public class ApplyPaymentPlan {
    @Schema(description = "主键")
    @TableId
    private Long id;

    @Schema(description = "合同编号")
    private String contractCode;

    @Schema(description = "步骤")
    private Integer step;

    @Schema(description = "支付方式")
    private Integer paymentMethod;

    @Schema(description = "起始点")
    private Integer dateType;

    @Schema(description = "起始日")
    private LocalDateTime startDate;

    @Schema(description = "天数")
    private Integer days;

    @Schema(description = "预计付款日")
    private LocalDateTime expectedReceiptDate;

    @Schema(description = "付款比例")
    private BigDecimal paymentRatio;

    @Schema(description = "应付金额")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount receivableAmount;

    @Schema(description = "实付金额")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount receivedAmount;

    @Schema(description = "已申请金额")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount appliedAmount;

    @Schema(description = "是否控制采购")
    private Integer controlPurchaseFlag;

    @Schema(description = "状态")
    private Integer exeStatus;

    @Schema(description = "变更标志", accessMode = Schema.AccessMode.READ_ONLY)
    @TableField(exist = false)
    private Integer changeFlag;

    @Schema(description = "货值总额")
    private BigDecimal goodsApplyAmount;

    @Schema(description = "本次申请金额")
    private BigDecimal applyAmount;

    @Schema(description = "实际付款比例")
    private BigDecimal realPaymentRatio;

    @Schema(description = "付款方式名称")
    private String paymentName;

    @Schema(description = "是否控制发票")
    private Integer controlInvoiceFlag;
}
