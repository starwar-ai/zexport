package com.syj.eplus.module.infra.controller.admin.companypath.vo;

import com.syj.eplus.framework.common.entity.JsonCompanyPath;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Schema(description = "管理后台 - 抛砖路径新增/修改 Request VO")
@Data
public class CompanyPathSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "5183")
    private Long id;

    @Schema(description = "路径", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "路径不能为空")
    private List<JsonCompanyPath> path;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;

    @Schema(description = "描述", requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;
}