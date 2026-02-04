package com.syj.eplus.module.dms.controller.admin.shipment.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShipmentToInvoiceSaveReq {

    @Schema(description = "出运明细id")
    private Long itemId;

    @Schema(description = "海关计量单位")
    private String hsMeasureUnit;

    @Schema(description = "开票数量")
    private BigDecimal invoiceQuantity;
}
