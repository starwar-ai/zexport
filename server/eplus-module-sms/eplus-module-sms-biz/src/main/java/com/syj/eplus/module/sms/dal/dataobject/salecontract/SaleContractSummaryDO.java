
package com.syj.eplus.module.sms.dal.dataobject.salecontract;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SaleContractSummaryDO {
    private BigDecimal sumTotalAmount; //金额
    private String currency; //币种
}

