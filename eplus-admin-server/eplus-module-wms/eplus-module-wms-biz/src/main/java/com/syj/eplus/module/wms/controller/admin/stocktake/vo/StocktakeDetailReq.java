package com.syj.eplus.module.wms.controller.admin.stocktake.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class StocktakeDetailReq {

    @Schema(description = "流程任务名", example = "eplus")
    private String processInstanceId;

    @Schema(description = "仓储管理-盘点单id", example = "1")
    private Long stocktakeId;

}