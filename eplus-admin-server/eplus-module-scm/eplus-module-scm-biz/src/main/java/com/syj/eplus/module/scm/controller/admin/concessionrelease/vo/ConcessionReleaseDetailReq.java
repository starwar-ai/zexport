package com.syj.eplus.module.scm.controller.admin.concessionrelease.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class ConcessionReleaseDetailReq {

    @Schema(description = "流程任务名", example = "eplus")
    private String processInstanceId;

    @Schema(description = "让步放行id", example = "1")
    private Long concessionReleaseId;

}