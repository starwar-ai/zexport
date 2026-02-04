package com.syj.eplus.module.oa.controller.admin.paymentapp.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 借款单明细 VO")
@Data
@ExcelIgnoreUnannotated
public class PaymentPrintDetailVO {

    @Schema(description = "发票内容")
    private String feeName;

    @Schema(description = "发票金额")
    private BigDecimal invoiceAmount;

    @Schema(description = "发票金额")
    private BigDecimal amount;

    @Schema(description = "费用归集描述")
    private String feeShareDesc;

}