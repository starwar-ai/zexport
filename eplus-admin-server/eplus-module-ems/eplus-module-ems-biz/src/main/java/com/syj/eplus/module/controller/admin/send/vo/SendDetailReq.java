package com.syj.eplus.module.controller.admin.send.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SendDetailReq {

    @Schema(description = "流程任务名", example = "eplus")
    private String processInstanceId;

    @Schema(description = "寄件id", example = "1")
    private Long senedId;

}
