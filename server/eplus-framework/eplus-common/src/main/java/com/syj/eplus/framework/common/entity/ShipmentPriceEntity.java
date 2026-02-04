package com.syj.eplus.framework.common.entity;

import lombok.Data;

@Data
public class ShipmentPriceEntity {

    /**
     * 发票号
     */
    private String invoiceCode;

    /**
     * 应收金额
     */
    private JsonAmount receivablePrice;

    /**
     * 是否收款完成
     */
    private Integer completedFlag;
}
