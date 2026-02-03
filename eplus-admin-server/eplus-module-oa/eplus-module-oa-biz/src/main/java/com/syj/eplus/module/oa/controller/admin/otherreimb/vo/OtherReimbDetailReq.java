package com.syj.eplus.module.oa.controller.admin.otherreimb.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class OtherReimbDetailReq {
    @Schema(description = "流程任务名", example = "eplus")
    private String processInstanceId;

    @Schema(description = "报销单id", example = "1")
    private Long otherReimbId;
}
