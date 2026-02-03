package com.syj.eplus.module.dms.controller.admin.shipment.vo;

import com.syj.eplus.module.dms.dal.dataobject.shipmentitem.ShipmentItem;
import lombok.Data;

import java.util.List;

@Data
public class BatchShipmentReq {

    private List<ShipmentItem> itemList;
}
