package com.syj.eplus.module.dms.controller.admin.shipment.vo.change;

import com.syj.eplus.module.dms.controller.admin.shipment.vo.ShipmentRespVO;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/29/20:01
 * @Description:
 */
@Data
public class ChangeShipmentSaveReq {
    private ShipmentRespVO old_shipment;
    private ShipmentRespVO shipment;

    private Integer submitFlag;
}
