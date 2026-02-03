package com.syj.eplus.module.infra.controller.admin.countryinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Schema(description = "管理后台 - 国家信息新增/修改 Request VO")
@Data
public class CountryInfoSaveReqVO {

    @Schema(description = "国家id", requiredMode = Schema.RequiredMode.REQUIRED, example = "8029")
    private Long id;

    @Schema(description = "国家名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @NotEmpty(message = "国家名称不能为空")
    private String name;

    @Schema(description = "国家编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "国家编码不能为空")
    private String code;

    @Schema(description = "区域编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "区域编码不能为空")
    private String regionCode;

    @Schema(description = "区域名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @NotEmpty(message = "区域名称不能为空")
    private String regionName;

}
