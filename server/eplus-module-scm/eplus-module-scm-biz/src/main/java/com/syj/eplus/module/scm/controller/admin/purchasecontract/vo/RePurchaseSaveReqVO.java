package com.syj.eplus.module.scm.controller.admin.purchasecontract.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 采购合同新增/修改 Request VO")
@Data
public class RePurchaseSaveReqVO {

    @Schema(description = "采购合同id")
    private Long id;

    @Schema(description = "重构原因")
    private String rePurchaseDesc;

}