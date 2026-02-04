package com.syj.eplus.module.dms.controller.admin.shipment.vo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@EqualsAndHashCode
public class AppendShipmentReq {

    private Long shipmentId;

    private List<Long> shipmentPlanIds;
}