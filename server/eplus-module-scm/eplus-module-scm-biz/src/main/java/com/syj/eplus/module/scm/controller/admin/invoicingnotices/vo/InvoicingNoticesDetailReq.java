package com.syj.eplus.module.scm.controller.admin.invoicingnotices.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class InvoicingNoticesDetailReq {

    @Schema(description = "流程任务名", example = "eplus")
    private String processInstanceId;

    @Schema(description = "开票通知id", example = "1")
    private Long invoicingNoticesId;


}