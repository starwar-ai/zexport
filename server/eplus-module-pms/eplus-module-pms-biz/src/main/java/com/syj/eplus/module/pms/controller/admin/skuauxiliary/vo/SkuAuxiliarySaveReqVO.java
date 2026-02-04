package com.syj.eplus.module.pms.controller.admin.skuauxiliary.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 产品辅料配比新增/修改 Request VO")
@Data
public class SkuAuxiliarySaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "8070")
    private Long id;

    @Schema(description = "产品编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "产品编号不能为空")
    private String skuCode;

    @Schema(description = "产品名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @NotEmpty(message = "产品名称不能为空")
    private String skuName;

    @Schema(description = "辅料编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "辅料编号不能为空")
    private String auxiliarySkuCode;

    @Schema(description = "辅料名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @NotEmpty(message = "辅料名称不能为空")
    private String auxiliarySkuName;

    @Schema(description = "产品比", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "产品比不能为空")
    private Integer skuRate;

    @Schema(description = "辅料比", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "辅料比不能为空")
    private Integer auxiliarySkuRate;

}