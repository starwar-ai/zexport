package com.syj.eplus.module.infra.controller.admin.sn.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 序列号记录新增/修改 Request VO")
@Data
public class SnSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "6470")
    private Long id;

    @Schema(description = "类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotEmpty(message = "类型不能为空")
    private String type;

    @Schema(description = "前缀", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotEmpty(message = "前缀不能为空")
    private String codePrefix;

    @Schema(description = "序列号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "序列号不能为空")
    private Integer sn;

    @Schema(description = "长度", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer length;

}