package com.syj.eplus.module.fms.controller.admin.payment.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.PaymentApplyEntity;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 财务付款表新增/修改 Request VO")
@Data
public class PaymentSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "28417")
    private Long id;

    @Schema(description = "支付编码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @Schema(description = "内部法人单位", example = "13149")
    private Long companyId;

    @Schema(description = "开户行", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotEmpty(message = "开户行不能为空")
    private String bank;

    @Schema(description = "银行账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "538")
//    @NotEmpty(message = "银行账号不能为空")
    private String bankAccount;

    @Schema(description = "开户行地址", example = "张三")
    private String bankAddress;

    @Schema(description = "支付状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
//    @NotNull(message = "支付状态不能为空")
    private Integer status;

    @Schema(description = "支付金额")
    private JsonAmount amount;

    @Schema(description = "支付日期")
    private LocalDateTime date;

    @Schema(description = "出纳员", example = "22506")
    private UserDept cashier;

    @Schema(description = "备注", example = "你说的对")
    private String remark;

    @Schema(description = "业务类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
//    @NotNull(message = "业务类型不能为空")
    private Integer businessType;

    @Schema(description = "业务编号", example = "30558")
    private String businessCode;

    @Schema(description = "支付对象类型", example = "30558")
    private Integer businessSubjectType;

    @Schema(description = "支付对象编号", example = "30558")
    private String businessSubjectCode;

    @Schema(description = "申请人id", example = "12015")
    private Long applyerId;

    @Schema(description = "打印状态")
    private Integer printFlag;

    @Schema(description = "打印次数")
    private Integer printTimes;

    @Schema(description = "审核状态")
    private Integer auditStatus;

    @Schema(description = "申请支付金额")
    private List<JsonAmount> applyAmount;

    @Schema(description = "最终审批人")
    private UserDept approver;

    @Schema(description = "审批时间")
    private LocalDateTime approvalTime;

    @Schema(description = "开户行联系人")
    private String bankPoc;

    @Schema(description = "银行行号")
    private String bankCode;

    private Integer submitFlag;

    @Schema(description = "申请单编号")
    private String applyCode;

    @Schema(description = "申请付款日期")
    private LocalDateTime applyPaymentDate;

    @Schema(description = "支付方式")
    private Integer paymentMethod;

    @Schema(description = "申请人")
    private UserDept applyer;

    @Schema(description = "付款申请信息")
    private PaymentApplyEntity paymentApply;

    @Schema(description = "对方银行")
    private String targetBank;

    @Schema(description = "对方账户")
    private String targetBankAccount;

    @Schema(description = "对方账号")
    private String targetBankPoc;

    @Schema(description = "链路编号列表")
    private List<String> linkCodeList;

    @Schema(description = "附件")
    private List<SimpleFile> annex;

    @Schema(description = "承兑天数")
    private Integer acceptanceDays;

    @Schema(description = "作废时间")
    private LocalDateTime cancelTime;

    @Schema(description = "作废原因")
    private String cancelReason;

    @Schema(description = "作废人")
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept cancelUser;

    @Schema(description = "付款银行")
    private String paymentBank;

    @Schema(description = "付款银行账号")
    private String paymentBankAccount;
}