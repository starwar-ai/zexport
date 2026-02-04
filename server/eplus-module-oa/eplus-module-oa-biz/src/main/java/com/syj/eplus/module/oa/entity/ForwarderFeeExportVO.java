package com.syj.eplus.module.oa.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ForwarderFeeExportVO {
    private String invoiceCode;

    /**
     * 预计结港时间
     */
    private String estDepartureTime;

    /**
     * 销售合同号
     */
    private String saleContractCode;

    /**
     * 采购合同编号
     */
    private String purchaseContractCode;

    /**
     * 基础产品编号
     */
    private String basicSkuCode;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 客户货号
     */
    private String cskuCode;
}
