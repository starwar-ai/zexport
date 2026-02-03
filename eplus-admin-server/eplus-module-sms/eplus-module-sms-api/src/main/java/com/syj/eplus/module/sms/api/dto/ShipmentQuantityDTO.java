package com.syj.eplus.module.sms.api.dto;

import lombok.Data;

/**
 * 外销合同新增/修改
 */
@Data
public class ShipmentQuantityDTO {

    private Long saleContractItemId;

    private Integer shippingQuantity;


}
