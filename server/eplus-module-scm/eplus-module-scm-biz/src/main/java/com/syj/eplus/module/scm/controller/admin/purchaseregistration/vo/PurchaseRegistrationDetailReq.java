package com.syj.eplus.module.scm.controller.admin.purchaseregistration.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class PurchaseRegistrationDetailReq {

    @Schema(description = "流程任务名", example = "eplus")
    private String processInstanceId;

    @Schema(description = "采购跟单登记id", example = "1")
    private Long purchaseRegistrationId;

}