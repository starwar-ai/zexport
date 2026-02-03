package com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class PurchasePlanItemDetailReq {

    @Schema(description = "流程任务名", example = "eplus")
    private String processInstanceId;

    @Schema(description = "采购计划明细id", example = "1")
    private Long purchasePlanItemId;

}