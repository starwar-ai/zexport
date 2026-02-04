package com.syj.eplus.module.scm.controller.admin.paymentapply.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class PaymentApplyDetailReq {

    @Schema(description = "流程任务名", example = "eplus")
    private String processInstanceId;

    @Schema(description = "付款申请主id", example = "1")
    private Long paymentApplyId;

}