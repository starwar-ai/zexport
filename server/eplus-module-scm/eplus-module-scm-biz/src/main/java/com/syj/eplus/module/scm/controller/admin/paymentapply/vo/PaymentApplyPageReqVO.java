package com.syj.eplus.module.scm.controller.admin.paymentapply.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 付款申请主分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PaymentApplyPageReqVO extends PageParam {
    @Schema(description = "付款申请编号")
    private String code;

    @Schema(description = "采购合同号")
    private String purchaseContractCode;

    @Schema(description = "付款计划id", example = "162")
    private Long paymentPlanId;

    @Schema(description = "下单主体主键", example = "26408")
    private Long companyId;

    @Schema(description = "下单主体", example = "王五")
    private String companyName;

    @Schema(description = "申请人id")
    private UserDept applyer;

    @Schema(description = "申请日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] applyDate;

    @Schema(description = "打印状态")
    private Integer printFlag;

    @Schema(description = "申请类型")
    private Integer step;

    @Schema(description = "货款总金额")
    private BigDecimal goodsTotalAmount;

    @Schema(description = "申请付款日")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] applyPaymentDate;

    @Schema(description = "申请备注", example = "你说的对")
    private String remark;

    @Schema(description = "应付供应商主键", example = "12494")
    private Long venderId;

    @Schema(description = "应付供应商编码")
    private String venderCode;

    @Schema(description = "应付供应商名称", example = "芋艿")
    private String venderName;

    @Schema(description = "应付币种")
    private String currency;

    @Schema(description = "付款方式id", example = "11025")
    private Long paymentId;

    @Schema(description = "付款方式名称", example = "张三")
    private String paymentName;

    @Schema(description = "税率")
    private BigDecimal taxRate;

    @Schema(description = "银行账号")
    private String bankAccount;

    @Schema(description = "开户行")
    private String bank;

    @Schema(description = "流程实例的编号", example = "21595")
    private String processInstanceId;

    @Schema(description = "流程实例状态")
    private String paymentPlan;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "审核状态", example = "2")
    private Integer auditStatus;

    @Schema(description = "付款状态")
    private Integer paymentStatus;

    @Schema(description = "付款备注类型")
    private Integer paymentMarkType;

    @Schema(description = "不包含的状态")
    private List<Integer> excludeAuditStatus;

    @Schema(description = "支付方式")
    private Integer paymentMethod;

    @Schema(description = "申请人id")
    private Long applyerId;

    @Schema(description = "申请人部门id")
    private Long applyerDeptId;

    @Schema(description = "申请总金额(大值)")
    private BigDecimal applyTotalAmountMax;

    @Schema(description = "申请总金额(小值)")
    private BigDecimal applyTotalAmountMin;
}