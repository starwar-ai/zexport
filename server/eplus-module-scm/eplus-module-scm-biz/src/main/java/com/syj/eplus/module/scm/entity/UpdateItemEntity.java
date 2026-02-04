package com.syj.eplus.module.scm.entity;

import lombok.Data;

@Data
public class UpdateItemEntity {

    /**
     * 销售合同编号
     */
    private String saleContractCode;

    /**
     * 产品编号
     */
    private String skuCode;

    /**
     * 供应商编号
     */
    private String venderCOde;

    /**
     * 采购员id
     */
    private Long purchaseUserId;

    /**
     * 转换标识
     */
    private Integer convertFlag;
    private Integer purchaseQuantity;
}
