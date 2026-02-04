package com.syj.eplus.module.scm.controller.admin.purchaseauxiliaryallocation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class PurchaseAuxiliaryAllocationDetailReq {

    @Schema(description = "流程任务名", example = "eplus")
    private String processInstanceId;

    @Schema(description = "采购合同辅料分摊id", example = "1")
    private Long purchaseAuxiliaryAllocationId;

}