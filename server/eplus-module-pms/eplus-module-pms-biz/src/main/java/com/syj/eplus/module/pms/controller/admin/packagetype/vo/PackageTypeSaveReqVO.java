package com.syj.eplus.module.pms.controller.admin.packagetype.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Schema(description = "管理后台 - 包装方式新增/修改 Request VO")
@Data
public class PackageTypeSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "23942")
    private Long id;

    @Schema(description = "包装方式code")
    private String code;

    @Schema(description = "包装方式名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @NotEmpty(message = "包装方式名称不能为空")
    private String name;

    @Schema(description = "包装方式英文名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "包装方式英文名称不能为空")
    private String nameEng;

    @Schema(description = "提交审核标识")
    private Integer submitFlag;
}