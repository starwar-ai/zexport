package com.syj.eplus.module.sms.controller.admin.quotation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class QuotationDetailReq {

    @Schema(description = "流程任务名", example = "eplus")
    private String processInstanceId;

    @Schema(description = "报价单主id", example = "1")
    private Long quotationId;

}