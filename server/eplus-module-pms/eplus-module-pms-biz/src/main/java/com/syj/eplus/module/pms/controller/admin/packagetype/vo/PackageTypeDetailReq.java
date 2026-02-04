package com.syj.eplus.module.pms.controller.admin.packagetype.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class PackageTypeDetailReq {

    @Schema(description = "流程任务名", example = "eplus")
    private String processInstanceId;

    @Schema(description = "包装方式id", example = "1")
    private Long packageTypeId;

}