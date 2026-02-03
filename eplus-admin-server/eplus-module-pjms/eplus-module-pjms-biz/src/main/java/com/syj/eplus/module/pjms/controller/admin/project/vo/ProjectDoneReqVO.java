package com.syj.eplus.module.pjms.controller.admin.project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 项目完成 Request VO")
@Data
public class ProjectDoneReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "31339")
    private Long id;


    @Schema(description = "实际日期")
    private LocalDateTime[] date;

}