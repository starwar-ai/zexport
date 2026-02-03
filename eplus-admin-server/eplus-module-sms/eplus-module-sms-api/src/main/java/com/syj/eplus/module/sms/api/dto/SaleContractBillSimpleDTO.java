package com.syj.eplus.module.sms.api.dto;

import lombok.Data;

@Data
public class SaleContractBillSimpleDTO {

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
