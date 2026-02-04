package com.syj.eplus.module.scm.api.purchasecontract.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InvoiceQuantityDTO {
    private Long saleContractItemId;

    private BigDecimal invoiceQuantity;

    private String venderCode;

    private String purchaseContractCode;

    private String skuCode;
}
