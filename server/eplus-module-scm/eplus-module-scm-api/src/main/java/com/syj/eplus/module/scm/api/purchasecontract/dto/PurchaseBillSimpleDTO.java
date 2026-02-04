package com.syj.eplus.module.scm.api.purchasecontract.dto;

import lombok.Data;

@Data
public class PurchaseBillSimpleDTO {

    /**
     * 唯一标识
     */
    private String uniqueCode;

    /**
     * 入库数量
     */
    private Integer quantity;

    /**
     * 异常状态
     */
    private Integer abnormalStatus;

    /**
     * 异常说明
     */
    private String abnormalRemark;

}
