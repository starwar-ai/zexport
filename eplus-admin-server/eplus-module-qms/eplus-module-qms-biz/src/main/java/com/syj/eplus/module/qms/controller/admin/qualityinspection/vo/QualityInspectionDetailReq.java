package com.syj.eplus.module.qms.controller.admin.qualityinspection.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class QualityInspectionDetailReq {

    @Schema(description = "流程任务名", example = "eplus")
    private String processInstanceId;

    @Schema(description = "验货单id", example = "1")
    private Long qualityInspectionId;

}