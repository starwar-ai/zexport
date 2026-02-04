package com.syj.eplus.module.fms.controller.admin.payment.vo;

import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.AmountConvert;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import cn.iocoder.yudao.framework.excel.core.convert.UserDeptConverter;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.PaymentApplyEntity;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.enums.DictTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 财务付款表 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PaymentRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "28417")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "支付编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("支付编码")
    private String code;

    @Schema(description = "内部法人单位id", example = "13149")
    private Long companyId;

    @Schema(description = "内部法人单位", example = "13149")
    @ExcelProperty("内部法人单位")
    private String companyName;

    @Schema(description = "开户行", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("开户行")
    private String bank;

    @Schema(description = "银行账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "538")
    @ExcelProperty("银行账号")
    private String bankAccount;

    @Schema(description = "开户行地址", example = "张三")
    @ExcelProperty("开户行地址")
    private String bankAddress;

    @Schema(description = "支付状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("支付状态")
    private Integer status;

    @Schema(description = "支付金额")
    @ExcelProperty(value = "支付金额", converter = AmountConvert.class)
    private JsonAmount amount;

    @Schema(description = "支付日期")
    @ExcelProperty("支付日期")
    private LocalDateTime date;

    @Schema(description = "出纳员", example = "22506")
    @ExcelProperty(value = "出纳员", converter = UserDeptConverter.class)
    private UserDept cashier;


    @Schema(description = "备注", example = "你说的对")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "业务类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty(value = "业务类型", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.BUSINESS_TYPE)
    private Integer businessType;

    @Schema(description = "业务编号", example = "30558")
    private String businessCode;

    @Schema(description = "业务名称", example = "30558")
    @ExcelProperty("业务名称")
    private String businessName;

    @Schema(description = "支付对象类型", example = "30558")
    @ExcelProperty(value = "支付对象类型", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.BUSINESS_SUBJECT_TYPE)
    private Integer businessSubjectType;

    @Schema(description = "支付对象编号", example = "30558")
    private String businessSubjectCode;

    @Schema(description = "支付对象名称", example = "30558")
    @ExcelProperty("支付对象")
    private String businessSubjectName;

    @Schema(description = "申请人", example = "12015")
    private UserDept applyer;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "打印状态")
    @ExcelProperty("打印状态")
    private Integer printFlag;

    @Schema(description = "打印次数")
    @ExcelProperty("打印次数")
    private Integer printTimes;

    @Schema(description = "审核状态")
    @ExcelProperty("审核状态")
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

    @Schema(description = "任务id")
    private String processInstanceId;

    @Schema(description = "申请单编号")
    private String applyCode;

    @Schema(description = "申请付款日期")
    private LocalDateTime applyPaymentDate;

    @Schema(description = "支付方式")
    private Integer paymentMethod;

    @Schema(description = "录入人")
    private UserDept inputUser;

    @Schema(description = "对方银行")
    private String targetBank;

    @Schema(description = "对方账户")
    private String targetBankAccount;

    @Schema(description = "对方账号")
    private String targetBankPoc;

    @Schema(description = "关联付款申请单")
    private PaymentApplyEntity paymentApply;

    @Schema(description = "链路编号列表")
    private List<String> linkCodeList;

    @Schema(description = "已付金额")
    private JsonAmount paidAmount;

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