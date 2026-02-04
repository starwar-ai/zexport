package com.syj.eplus.module.scm.dal.dataobject.purchaseplanitem;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseItemSummaryDO {

    private Integer sumQty; //数量
    private BigDecimal sumTotal; //金额
    private String currency; //币种

}

