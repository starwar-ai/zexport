package com.syj.eplus.module.pms.controller.admin.sku.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class SkuDetailReq {

    @Schema(description = "流程任务名", example = "eplus")
    private String processInstanceId;

    @Schema(description = "skuId", example = "1")
    private Long skuId;

    @Schema(description = "skuCode", example = "1")
    private String skuCode;
}