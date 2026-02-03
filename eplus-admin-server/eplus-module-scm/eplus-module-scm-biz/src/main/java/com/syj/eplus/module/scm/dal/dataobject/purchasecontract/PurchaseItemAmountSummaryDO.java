package com.syj.eplus.module.scm.dal.dataobject.purchasecontract;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseItemAmountSummaryDO {

    private String currency;

    private BigDecimal amount;
}
