package com.syj.eplus.module.dpms.controller.admin.reportrole.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 报表角色关系表新增/修改 Request VO")
@Data
public class ReportRoleSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "3773")
    private Long id;

    @Schema(description = "角色编号", example = "30354")
    private Long roleId;

    @Schema(description = "报表编号", example = "8413")
    private Long reportId;

}