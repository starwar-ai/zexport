package com.syj.eplus.module.pjms.controller.admin.project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class ProjectDetailReq {

    @Schema(description = "流程任务名", example = "eplus")
    private String processInstanceId;

    @Schema(description = "项目id", example = "1")
    private Long projectId;

}