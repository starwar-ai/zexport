package com.syj.eplus.module.infra.controller.admin.card.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.SimpleFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 首页卡片 Response VO")
@Data
@ExcelIgnoreUnannotated
public class CardRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "25665")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @ExcelProperty("名称")
    private String title;

    @Schema(description = "描述", requiredMode = Schema.RequiredMode.REQUIRED, example = "随便")
    @ExcelProperty("描述")
    private String description;

    @Schema(description = "图片", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("图片")
    private SimpleFile picture;

    @Schema(description = "接口url", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.iocoder.cn")
    @ExcelProperty("接口url")
    private String url;

    @Schema(description = "是否启用", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("是否启用")
    private Integer status;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

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