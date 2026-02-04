package com.syj.eplus.module.wms.controller.admin.adjustment.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class AdjustmentDetailReq {

    @Schema(description = "仓储管理-盘库调整单id", example = "1")
    private Long adjustmentId;

}
