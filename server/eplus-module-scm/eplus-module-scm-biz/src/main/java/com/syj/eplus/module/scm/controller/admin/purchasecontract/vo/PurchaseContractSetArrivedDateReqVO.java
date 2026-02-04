package com.syj.eplus.module.scm.controller.admin.purchasecontract.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 采购合同设置到料时间 Request VO")
@Data
public class PurchaseContractSetArrivedDateReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "32529")
    private Long id;

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "32529")
    private LocalDateTime date;
}