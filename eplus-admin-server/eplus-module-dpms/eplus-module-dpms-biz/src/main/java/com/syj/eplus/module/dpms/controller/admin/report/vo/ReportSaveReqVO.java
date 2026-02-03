package com.syj.eplus.module.dpms.controller.admin.report.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 报表配置新增/修改 Request VO")
@Data
public class ReportSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "4930")
    private Long id;

    @Schema(description = "用户编号", example = "7833")
    private Long userId;

    @Schema(description = "角色编号", example = "10374")
    private Long roleId;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    private String name;

    @Schema(description = "类型", example = "1")
    private Integer type;

    @Schema(description = "显示顺序", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer sort;

    @Schema(description = "组件编号")
    private String componentId;
}