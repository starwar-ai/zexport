package com.syj.eplus.module.dms.controller.admin.shipment.vo;

import lombok.Data;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/17 16:37
 */
@Data
public class CloseShipmentReq {

    private Long parentId;

    private Long itemId;
}
