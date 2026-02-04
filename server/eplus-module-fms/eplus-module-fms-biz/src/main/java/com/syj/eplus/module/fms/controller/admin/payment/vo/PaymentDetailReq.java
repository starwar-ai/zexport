package com.syj.eplus.module.fms.controller.admin.payment.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PaymentDetailReq {
    @Schema(description = "流程任务名", example = "eplus")
    private String processInstanceId;

    @Schema(description = "财务付款单id", example = "1")
    private Long paymentId;
}
