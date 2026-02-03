package com.syj.eplus.module.scm.controller.admin.purchasecontract.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PlaceOrderReq {

    @Schema(description = "采购合同")
    private Long contractId;

    @Schema(description = "下单时间")
    private LocalDateTime placeOrderTime;

}
