package com.syj.eplus.module.fms.controller.admin.receipt.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class ReceiptDetailReq {

    @Schema(description = "流程任务名", example = "eplus")
    private String processInstanceId;

    @Schema(description = "财务收款单id", example = "1")
    private Long receiptId;

}