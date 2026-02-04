package com.syj.eplus.module.scm.controller.admin.paymentapply.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.scm.dal.dataobject.addsubterm.PurchaseAddSubTerm;
import com.syj.eplus.module.scm.dal.dataobject.purchaseregistrationitem.PurchaseRegistrationItem;
import com.syj.eplus.module.scm.entity.ApplyPaymentPlan;
import com.syj.eplus.module.scm.entity.ApplyerPurchaseContractItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 付款申请主 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PaymentApplyRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "25193")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "编号")
    private String code;

    @Schema(description = "付款计划id", example = "162")
    @ExcelProperty("付款计划id")
    private Long paymentPlanId;

    @Schema(description = "下单主体主键", example = "26408")
    @ExcelProperty("下单主体主键")
    private Long companyId;

    @Schema(description = "下单主体", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @ExcelProperty("下单主体")
    private String companyName;

    @Schema(description = "申请人id", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("申请人id")
    private UserDept applyer;

    @Schema(description = "申请日期")
    @ExcelProperty("申请日期")
    private LocalDateTime applyDate;

    @Schema(description = "打印状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("打印状态")
    private Integer printFlag;

    @Schema(description = "申请类型")
    @ExcelProperty("申请类型")
    private Integer step;

    @Schema(description = "申请总金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("申请总金额")
    private BigDecimal applyTotalAmount;

    @Schema(description = "货款总金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("货款总金额")
    private BigDecimal goodsTotalAmount;

    @Schema(description = "申请付款日")
    @ExcelProperty("申请付款日")
    private LocalDateTime applyPaymentDate;

    @Schema(description = "申请备注", requiredMode = Schema.RequiredMode.REQUIRED, example = "你说的对")
    @ExcelProperty("申请备注")
    private String remark;

    @Schema(description = "应付供应商主键", example = "12494")
    @ExcelProperty("应付供应商主键")
    private Long venderId;

    @Schema(description = "应付供应商编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("应付供应商编码")
    private String venderCode;

    @Schema(description = "应付供应商名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @ExcelProperty("应付供应商名称")
    private String venderName;

    @Schema(description = "应付币种", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("应付币种")
    private String currency;

    @Schema(description = "付款方式id", example = "11025")
    @ExcelProperty("付款方式id")
    private Long paymentId;

    @Schema(description = "付款方式名称", example = "张三")
    @ExcelProperty("付款方式名称")
    private String paymentName;

    @Schema(description = "税率")
    @ExcelProperty("税率")
    private BigDecimal taxRate;

    @Schema(description = "银行账号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("银行账号")
    private String bankAccount;

    @Schema(description = "开户行", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("开户行")
    private String bank;

    @Schema(description = "流程实例的编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "21595")
    @ExcelProperty("流程实例的编号")
    private String processInstanceId;

    @Schema(description = "流程实例状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("流程实例状态")
    private String paymentPlan;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "审核状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("审核状态")
    private Integer auditStatus;

    @Schema(description = "付款计划信息")
    List<ApplyPaymentPlan> applyPaymentPlanList;

    @Schema(description = "采购明细")
    List<ApplyerPurchaseContractItem> applyerPurchaseItemList;

    @Schema(description = "采购合同编号列表")
    private List<String> purchaseContractCodeList;

    @Schema(description = "关联发票")
    private List<PurchaseRegistrationItem> registrationItemList;

    @Schema(description = "付款日期")
    private LocalDateTime paymentDate;

    @Schema(description = "付款人")
    private UserDept paymentUser;

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

    @Schema(description = "加减项")
    private List<PurchaseAddSubTerm> purchaseAddSubTermList;

    @Schema(description = "链路编号列表")
    private List<String> linkCodeList;

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

    @Schema(description = "作废时间")
    private LocalDateTime cancelTime;

    @Schema(description = "作废原因")
    private String cancelReason;

    @Schema(description = "作废人")
    private UserDept cancelUser;
}