package com.syj.eplus.module.sms.api.dto;

import lombok.Data;

@Data
public class CloseShipmentDTO {

    private  Long saleContractItemId;

    private Integer shipmentQuantity;

}
