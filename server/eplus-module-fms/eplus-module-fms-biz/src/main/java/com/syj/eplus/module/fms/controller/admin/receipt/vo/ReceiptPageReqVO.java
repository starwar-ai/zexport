package com.syj.eplus.module.fms.controller.admin.receipt.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 财务收款单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ReceiptPageReqVO extends PageParam {

    @Schema(description = "申请单号")
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
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] receiptTime;

    @Schema(description = "收款备注", example = "随便")
    private String remark;

    @Schema(description = "收款人", example = "16641")
    private UserDept receiptUser;

    @Schema(description = "最终审批人")
    private UserDept approver;

    @Schema(description = "审批时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] approvalTime;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "业务类型")
    private Integer businessType;

    @Schema(description = "业务编号")
    private String businessCode;

    @Schema(description = "业务类型")
    private Integer businessSubjectType;
}