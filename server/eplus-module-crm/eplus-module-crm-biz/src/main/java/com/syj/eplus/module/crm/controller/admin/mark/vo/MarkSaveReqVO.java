package com.syj.eplus.module.crm.controller.admin.mark.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Schema(description = "管理后台 - 唛头新增/修改 Request VO")
@Data
public class MarkSaveReqVO {


    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "唛头名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @NotEmpty(message = "唛头名称不能为空")
    private String name;

    @Schema(description = "唛头英文名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @NotEmpty(message = "唛头英文名称不能为空")
    private String engName;

    @Schema(description = "主文字唛", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "主文字唛不能为空")
    private String mainMarkText;

    @Schema(description = "主图形唛", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "主图形唛不能为空")
    private String mainMarkPic;

    @Schema(description = "主侧文字唛", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "主侧文字唛不能为空")
    private String mainMarkTextSide;

    @Schema(description = "主侧图形唛", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "主侧图形唛不能为空")
    private String mainMarkPicSide;

    @Schema(description = "内主文字唛", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "内主文字唛不能为空")
    private String mainMarkTextIn;

    @Schema(description = "内主图形唛", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "内主图形唛不能为空")
    private String mainMarkPicIn;

    @Schema(description = "内侧文字唛", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "内侧文字唛不能为空")
    private String mainMarkTextSideIn;

    @Schema(description = "内侧图形唛", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "内侧图形唛不能为空")
    private String mainMarkPicSideIn;

}