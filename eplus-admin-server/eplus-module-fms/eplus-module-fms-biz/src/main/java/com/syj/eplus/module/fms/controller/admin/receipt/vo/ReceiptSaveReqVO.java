package com.syj.eplus.module.fms.controller.admin.receipt.vo;

import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.enums.DictTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 财务收款单新增/修改 Request VO")
@Data
public class ReceiptSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "12052")
    private Long id;

    @Schema(description = "申请单号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @Schema(description = "打印状态")
    private Integer printFlag;

    @Schema(description = "打印次数")
    private Integer printTimes;

    @Schema(description = "审核状态", example = "2")
    private Integer auditStatus;

    @Schema(description = "内部法人单位编号", example = "23699")
    private Long companyId;

    @Schema(description = "开户行")
    private String bank;

    @Schema(description = "开户行地址")
    private String bankAddress;

    @Schema(description = "银行账号", example = "15782")
    private String bankAccount;

    @Schema(description = "开户行联系人")
    private String bankPoc;

    @Schema(description = "银行行号")
    private String bankCode;

    @Schema(description = "收款金额")
    private JsonAmount amount;

    @Schema(description = "实时汇率")
    private JsonAmount rate;

    @Schema(description = "收款时间")
    private LocalDateTime receiptTime;

    @Schema(description = "收款备注", example = "随便")
    private String remark;

    @Schema(description = "收款人", example = "16641")
    private UserDept receiptUser;

    @Schema(description = "最终审批人")
    private UserDept approver;

    @Schema(description = "审批时间")
    private LocalDateTime approvalTime;

    @Schema(description = "提交审核标识")
    private Integer submitFlag;

    @Schema(description = "业务类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty(value = "业务类型", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.BUSINESS_TYPE)
    private Integer businessType;

    @Schema(description = "业务编号", example = "30558")
    private String businessCode;

    @Schema(description = "支付对象类型")
    private Integer businessSubjectType;

    @Schema(description = "支付对象编号")
    private String businessSubjectCode;

    @Schema(description = "收款状态")
    private Integer status;

    @Schema(description = "收款方式")
    @ExcelProperty(value = "收款方式", converter = DictConvert.class)
    @DictFormat("loan_type")
    private Integer receiptType;

    @Schema(description = "流程发起人id")
    private Long startUserId;

}