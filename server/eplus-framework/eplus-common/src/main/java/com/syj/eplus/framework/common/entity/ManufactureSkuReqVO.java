package com.syj.eplus.framework.common.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Desc——加工产品请求参数
 * Create by Rangers at  2024-10-28 16:59
 */
@Data
@ToString
@EqualsAndHashCode
public class ManufactureSkuReqVO {

    /**
     * 采购合同编号
     */
    String purchaseContractCode;

    /**
     * 产品编码
     */
    String skuCode;

    /**
     * 加工数量
     */
    Integer processingNum;

    /**
     * 销售明细id
     */
    private Long saleContractItemId;
}
