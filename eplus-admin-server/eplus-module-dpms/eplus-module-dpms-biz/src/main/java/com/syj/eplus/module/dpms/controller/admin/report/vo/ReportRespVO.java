package com.syj.eplus.module.dpms.controller.admin.report.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 报表配置 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ReportRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "4930")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "用户编号", example = "7833")
    @ExcelProperty("用户编号")
    private Long userId;

    @Schema(description = "角色编号", example = "10374")
    @ExcelProperty("角色编号")
    private Long roleId;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @ExcelProperty("名称")
    private String name;

    @Schema(description = "类型", example = "1")
    @ExcelProperty("类型")
    private Integer type;

    @Schema(description = "显示顺序", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("显示顺序")
    private Integer sort;

    @Schema(description = "组件编号")
    private String componentId;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}