package com.syj.eplus.module.oa.controller.admin.paymentapp.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 借款单明细 VO")
@Data
@ExcelIgnoreUnannotated
public class LoanDescVO {

    @Schema(description = "序号")
    private Integer sortNum;

    @Schema(description = "供应商编号")
    private String venderCode;

    @Schema(description = "供应商名称")
    private String venderName;

    @Schema(description = "欠款总金额")
    private JsonAmount amount;

    @Schema(description = "待转金额")
    private JsonAmount notRepayAmount;

    @Schema(description = "欠款日期")
    private LocalDateTime loanDate;

    @Schema(description = "欠款截止日")
    private LocalDateTime deadlineTime;

    @Schema(description = "还款状态")
    private Integer repayStatus;

    @Schema(description = "本次是否还款")
    private Integer thisRepayFlag;

    @Schema(description = "本次还款金额")
    private JsonAmount thisRepayAmount;


}