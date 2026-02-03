package com.syj.eplus.module.sms.controller.admin.saleauxiliaryallocation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class SaleAuxiliaryAllocationDetailReq {

    @Schema(description = "流程任务名", example = "eplus")
    private String processInstanceId;

    @Schema(description = "销售合同辅料分摊id", example = "1")
    private Long saleAuxiliaryAllocationId;

}