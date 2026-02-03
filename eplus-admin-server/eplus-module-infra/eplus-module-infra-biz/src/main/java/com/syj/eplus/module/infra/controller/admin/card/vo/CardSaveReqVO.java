package com.syj.eplus.module.infra.controller.admin.card.vo;

import com.syj.eplus.framework.common.entity.SimpleFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 首页卡片新增/修改 Request VO")
@Data
public class CardSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "25665")
    private Long id;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @NotEmpty(message = "名称不能为空")
    private String title;

    @Schema(description = "描述", requiredMode = Schema.RequiredMode.REQUIRED, example = "随便")
    @NotEmpty(message = "描述不能为空")
    private String description;

    @Schema(description = "图片", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "图片不能为空")
    private SimpleFile picture;

    @Schema(description = "是否启用", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer status;

    @Schema(description = "唯一标识")
    private String uniqueCode;

    @Schema(description = "页签id")
    private Long tabId;

    @Schema(description = "X轴")
    private Integer x;

    @Schema(description = "Y轴")
    private Integer y;

    @Schema(description = "高度")
    private Integer h;

    @Schema(description = "宽度")
    private Integer w;

    @Schema(description = "条件")
    private String filterCondition;

    @Schema(description = "类型")
    private String type;

    @Schema(description = "组件")
    private String currentComponent;

    @Schema(description = "标题标识")
    private Boolean titleFlag;

    @Schema(description = "是否基础卡片")
    private Integer basicFlag;
}