package com.syj.eplus.module.dms.controller.admin.shipment.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShipmentReq {

    private Long id;

    private LocalDateTime estDepartureTime;
}
