package com.syj.eplus.module.sms.controller.admin.salecontract.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;


@Data
@ToString
public class SaleContractDetailReq {

    @Schema(description = "流程任务名", example = "eplus")
    private String processInstanceId;

    @Schema(description = "外销合同id", example = "1")
    private Long saleContractId;

    @Schema(description = "外销合同编号", example = "1")
    private String saleContractCode;
}