package com.syj.eplus.module.oa.controller.admin.loanapp.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 借款申请单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LoanAppPageReqVO extends PageParam {

    @Schema(description = "单据编号")
    private String code;

    @Schema(description = "打印状态")
    private Integer printFlag;

    @Schema(description = "打印次数")
    private Integer printTimes;

    @Schema(description = "审核状态", example = "2")
    private Integer auditStatus;

    @Schema(description = "借款事由")
    private String purpose;

    @Schema(description = "申请部门", example = "8228")
    private Long loanDeptId;

    @Schema(description = "申请人编号", example = "24078")
    private Long applyerId;

    @Schema(description = "借款金额")
    private JsonAmount amount;

    @Schema(description = "借款申请日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] loanDate;

    @Schema(description = "借款方式", example = "2")
    private Integer loanType;

    @Schema(description = "支付状态", example = "1")
    private Integer paymentStatus;

    @Schema(description = "已转金额")
    private JsonAmount paymentAmount;

    @Schema(description = "支付日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] paymentDate;

    @Schema(description = "还款状态", example = "2")
    private Integer repayStatus;

    @Schema(description = "已还金额")
    private JsonAmount repayAmount;

    @Schema(description = "实际还款日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] repayDate;

    @Schema(description = "剩余未还款金额")
    private JsonAmount outstandingAmount;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "借款类型")
    private Integer loanSource;

    @Schema(description = "附件")
    private LocalDateTime annex;

    @Schema(description = "借款状态")
    private Integer loanStatus;

    @Schema(description = "截止时间")
    private LocalDateTime deadlineTime;

    @Schema(description = "采购合同编号")
    private String purchaseContractCode;

    @Schema(description = "供应商id")
    private Long venderId;

    @Schema(description = "供应编号")
    private String venderCode;

    @Schema(description = "供应商名称")
    private String venderName;

    @Schema(description = "借款主体")
    private Long companyId;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "剩余还款还金额不为0")
    private Integer outstandingAmountNotZeroFlag;

}