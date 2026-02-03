package com.syj.eplus.module.scm.controller.admin.paymentapply.vo;

import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.scm.dal.dataobject.addsubterm.PurchaseAddSubTerm;
import com.syj.eplus.module.scm.entity.ApplyPaymentPlan;
import com.syj.eplus.module.scm.entity.ApplyerPurchaseContractItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 付款申请主新增/修改 Request VO")
@Data
public class PaymentApplySaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "25193")
    private Long id;

    @Schema(description = "编号")
    private String code;

    @Schema(description = "付款计划id", example = "162")
    private Long paymentPlanId;

    @Schema(description = "下单主体主键", example = "26408")
    private Long companyId;

    @Schema(description = "下单主体", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    private String companyName;

    @Schema(description = "申请人id", requiredMode = Schema.RequiredMode.REQUIRED)
    private UserDept applyer;

    @Schema(description = "申请日期")
    private LocalDateTime applyDate;

    @Schema(description = "打印状态", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer printFlag;

    @Schema(description = "申请类型")
    private Integer step;

    @Schema(description = "申请总金额", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal applyTotalAmount;

    @Schema(description = "货款总金额", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal goodsTotalAmount;

    @Schema(description = "申请付款日")
    private LocalDateTime applyPaymentDate;

    @Schema(description = "申请备注", requiredMode = Schema.RequiredMode.REQUIRED, example = "你说的对")
    private String remark;

    @Schema(description = "应付供应商主键", example = "12494")
    private Long venderId;

    @Schema(description = "应付供应商编码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String venderCode;

    @Schema(description = "应付供应商名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    private String venderName;

    @Schema(description = "应付币种", requiredMode = Schema.RequiredMode.REQUIRED)
    private String currency;

    @Schema(description = "付款方式id", example = "11025")
    private Long paymentId;

    @Schema(description = "付款方式名称", example = "张三")
    private String paymentName;

    @Schema(description = "税率")
    private BigDecimal taxRate;

    @Schema(description = "银行账号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String bankAccount;

    @Schema(description = "开户行", requiredMode = Schema.RequiredMode.REQUIRED)
    private String bank;

    @Schema(description = "流程实例的编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "21595")
    private String processInstanceId;

    @Schema(description = "流程实例状态", requiredMode = Schema.RequiredMode.REQUIRED)
    private String paymentPlan;

    @Schema(description = "审核状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer auditStatus;

    @Schema(description = "提交审核标识")
    private Integer submitFlag;

    @Schema(description = "付款计划信息")
    private List<ApplyPaymentPlan> applyPaymentPlanList;

    @Schema(description = "采购明细")
    private List<ApplyerPurchaseContractItem> applyerPurchaseItemList;

    @Schema(description = "加减项")
    private List<PurchaseAddSubTerm> purchaseAddSubTermList;

    @Schema(description = "采购合同编号列表")
    private List<String> purchaseContractCodeList;

    @Schema(description = "付款状态")
    private Integer paymentStatus;

    @Schema(description = "实际付款金额")
    private BigDecimal realPaymentAmount;

    @Schema(description = "银行账号")
    private String venderBankAccount;

    @Schema(description = "开户行")
    private String venderBank;

    @Schema(description = "开户行联系人")
    private String venderBankPoc;

    @Schema(description = "采购员")
    private List<UserDept> purchaseUserList;

    @Schema(description = "付款备注类型")
    private Integer paymentMarkType;

    @Schema(description = "附件")
    private List<SimpleFile> annex;

    @Schema(description = "承兑天数")
    private Integer acceptanceDays;

    @Schema(description = "支付方式")
    private Integer paymentMethod;

    @Schema(description = "加减项总金额")
    private JsonAmount subAddTotalAmount;
}