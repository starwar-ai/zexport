package com.syj.eplus.module.scm.entity;

import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import cn.iocoder.yudao.framework.mybatis.core.exinterface.ChangeExInterface;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/05/16:00
 * @Description:
 */
@Data
@Accessors(chain = false)
@Schema(description = "付款计划")
public class PurchasePaymentPlanChange implements ChangeExInterface {
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
    private JsonAmount receivableAmount;

    @Schema(description = "实付金额")
    private JsonAmount receivedAmount;

    @Schema(description = "已申请金额")
    private JsonAmount appliedAmount;

    @Schema(description = "是否控制采购")
    private Integer controlPurchaseFlag;

    @Schema(description = "状态")
    private Integer exeStatus;

    @Schema(description = "旧步骤")
    private Integer old_step;

    @Schema(description = "旧支付方式")
    private Integer old_paymentMethod;

    @Schema(description = "旧起始点")
    private Integer old_dateType;

    @Schema(description = "旧起始日")
    private LocalDateTime old_startDate;

    @Schema(description = "旧天数")
    private Integer old_days;

    @Schema(description = "旧预计付款日")
    private LocalDateTime old_expectedReceiptDate;

    @Schema(description = "旧付款比例")
    private BigDecimal old_paymentRatio;

    @Schema(description = "旧应付金额")
    private JsonAmount old_receivableAmount;

    @Schema(description = "旧实付金额")
    private JsonAmount old_receivedAmount;

    @Schema(description = "旧已申请金额")
    private JsonAmount old_appliedAmount;

    @Schema(description = "旧是否控制采购")
    private Integer old_controlPurchaseFlag;

    private Integer changeFlag;

    @TableField(exist = false)
    private String sourceCode;

    @TableField(exist = false)
    private List<JsonEffectRange> effectRangeList;
}
