package com.syj.eplus.framework.common.entity;

import lombok.Data;

@Data
public class SimpleContractDetailDTO {
    /**
     * 采购类型
     */
    private String purchaseContractType;

    /**
     * 合同号
     */
    private String contractCode;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 使用数量
     */
    private Integer usedQuantity;

    /**
     * 开票状态
     */
    private String invoicingStatus;

    /**
     * 开票数量
     */
    private Integer invoicedQuantity;

    /**
     * 开票品名
     */
    private String invoicedItemName;

    /**
     * 采购单价（含税价）
     * 本次采购：从采购合同明细中获取含税单价
     * 库存采购：从库存表中获取价格
     */
    private JsonAmount price;

    private Integer autoFlag;
}
