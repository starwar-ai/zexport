package com.syj.eplus.module.dtms.controller.admin.design.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class DesignDetailReq {

    @Schema(description = "流程任务名", example = "eplus")
    private String processInstanceId;

    @Schema(description = "设计-任务单id", example = "1")
    private Long designId;

}
