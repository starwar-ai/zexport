package com.syj.eplus.module.scm.controller.admin.invoicingnotices.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class InvoicingNoticesPrintSku {

    @Schema(description = "报关时间")
    private String date;

    @Schema(description = "采购合同编号")
    private String purchaseContractCode;

    @Schema(description = "客户PO")
    private String custPo;

    @Schema(description = "发票品名")
    private String skuName;

    @Schema(description = "计量单位")
    private String unit;

    @Schema(description = "数量")
    private Integer quantity;

    @Schema(description = "单价")
    private BigDecimal price;

    @Schema(description = "税率")
    private BigDecimal taxRate;

    @Schema(description = "总金额")
    private BigDecimal totalAmount;
}