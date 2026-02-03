package com.syj.eplus.module.fms.controller.admin.receipt.vo;

import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.AmountConvert;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import cn.iocoder.yudao.framework.excel.core.convert.UserDeptConverter;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.enums.DictTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
@Schema(description = "管理后台 - 财务收款单 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ReceiptRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "12052")
    @ExcelProperty("主键")
    private Long id;
    
    @Schema(description = "申请单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("申请单号")
    private String code;

    @Schema(description = "申请人", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("申请人")
    private UserDept applyer;
    
    @Schema(description = "打印状态")
    @ExcelProperty("打印状态")
    private Integer printFlag;
    
    @Schema(description = "打印次数")
    @ExcelProperty("打印次数")
    private Integer printTimes;
    
    @Schema(description = "审核状态", example = "2")
    @ExcelProperty("审核状态")
    private Integer auditStatus;
    
    @Schema(description = "内部法人单位编号", example = "23699")
    private Long companyId;

    @Schema(description = "内部法人单位名称", example = "23699")
    @ExcelProperty("内部法人单位名称")
    private String companyName;
    
    @Schema(description = "开户行")
    @ExcelProperty("开户行")
    private String bank;
    
    @Schema(description = "开户行地址")
    @ExcelProperty("开户行地址")
    private String bankAddress;
    
    @Schema(description = "银行账号", example = "15782")
    @ExcelProperty("银行账号")
    private String bankAccount;
    
    @Schema(description = "开户行联系人")
    @ExcelProperty("开户行联系人")
    private String bankPoc;
    
    @Schema(description = "银行行号")
    @ExcelProperty("银行行号")
    private String bankCode;

    @Schema(description = "收款金额")
    @ExcelProperty(value = "收款金额", converter = AmountConvert.class)
    private JsonAmount amount;
    
    @Schema(description = "实时汇率")
    @ExcelProperty("实时汇率")
    private JsonAmount rate;
    
    @Schema(description = "收款时间")
    @ExcelProperty("收款时间")
    private LocalDateTime receiptTime;
    
    @Schema(description = "收款备注", example = "随便")
    @ExcelProperty("收款备注")
    private String remark;
    
    @Schema(description = "收款人", example = "16641")
    @ExcelProperty(value = "收款人", converter = UserDeptConverter.class)
    private UserDept receiptUser;
    
    @Schema(description = "最终审批人")
    @ExcelProperty(value = "最终审批人", converter = UserDeptConverter.class)
    private UserDept approver;
    
    @Schema(description = "审批时间")
    @ExcelProperty("审批时间")
    private LocalDateTime approvalTime;
    
    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "业务类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty(value = "业务类型", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.BUSINESS_TYPE)
    private Integer businessType;

    @Schema(description = "业务编号", example = "30558")
    private String businessCode;

    @Schema(description = "支付对象类型", example = "30558")
    @ExcelProperty(value = "支付对象类型", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.BUSINESS_SUBJECT_TYPE)
    private Integer businessSubjectType;

    @Schema(description = "支付对象编号", example = "30558")
    private String businessSubjectCode;

    @Schema(description = "支付对象名称", example = "30558")
    @ExcelProperty("支付对象名称")
    private String businessSubjectName;
    @Schema(description = "任务id")

    private String processInstanceId;

    @Schema(description = "收款状态")
    @ExcelProperty(value = "收款状态", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.RECEIPT_STATUS)
    private Integer status;

    @Schema(description = "收款方式")
    @ExcelProperty(value = "收款方式", converter = DictConvert.class)
    @DictFormat("loan_type")
    private Integer receiptType;
    
}