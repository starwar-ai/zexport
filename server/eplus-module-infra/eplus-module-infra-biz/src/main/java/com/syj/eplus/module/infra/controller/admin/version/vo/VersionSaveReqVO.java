package com.syj.eplus.module.infra.controller.admin.version.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 版本记录新增/修改 Request VO")
@Data
public class VersionSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "20419")
    private Long id;

    @Schema(description = "前端版本", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "前端版本不能为空")
    private String frontVer;

    @Schema(description = "后端版本", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "后端版本不能为空")
    private String serverVer;

    @Schema(description = "发布版本", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "发布版本不能为空")
    private String publishVer;

    @Schema(description = "发布更新明细", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @NotEmpty(message = "发布更新明细不能为空")
    private String publishName;

    @Schema(description = "前端更新明细", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "前端更新明细不能为空")
    private String frontDesc;

    @Schema(description = "前端更新明细", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "前端更新明细不能为空")
    private String serverDesc;

    @Schema(description = "发布更新明细", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "发布更新明细不能为空")
    private String publishDesc;

    @Schema(description = "是否显示", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否显示不能为空")
    private Integer enabled;

}