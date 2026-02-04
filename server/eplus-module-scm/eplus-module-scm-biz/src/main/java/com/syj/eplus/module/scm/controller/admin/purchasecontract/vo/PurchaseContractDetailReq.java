package com.syj.eplus.module.scm.controller.admin.purchasecontract.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class PurchaseContractDetailReq {

    @Schema(description = "流程任务名", example = "eplus")
    private String processInstanceId;

    @Schema(description = "采购合同id", example = "1")
    private Long purchaseContractId;


}