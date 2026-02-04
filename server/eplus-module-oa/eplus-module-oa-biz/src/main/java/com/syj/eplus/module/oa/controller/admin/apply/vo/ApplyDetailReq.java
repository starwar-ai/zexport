package com.syj.eplus.module.oa.controller.admin.apply.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class ApplyDetailReq {

    @Schema(description = "流程任务名", example = "eplus")
    private String processInstanceId;

    @Schema(description = "OA申请单id", example = "1")
    private Long applyId;

}