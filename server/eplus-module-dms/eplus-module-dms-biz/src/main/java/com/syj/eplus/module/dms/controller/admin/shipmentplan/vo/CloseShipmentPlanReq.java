package com.syj.eplus.module.dms.controller.admin.shipmentplan.vo;

import lombok.Data;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/22 19:36
 */
@Data
public class CloseShipmentPlanReq {

    private Long parentId;

    private Long itemId;

    private String remark;
}
