package com.syj.eplus.module.infra.controller.admin.version.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
@Schema(description = "管理后台 - 版本记录 Response VO")
@Data
@ExcelIgnoreUnannotated
public class VersionRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "20419")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "前端版本", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("前端版本")
    private String frontVer;

    @Schema(description = "后端版本", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("后端版本")
    private String serverVer;

    @Schema(description = "发布版本", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("发布版本")
    private String publishVer;

    @Schema(description = "发布更新明细", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @ExcelProperty("发布更新明细")
    private String publishName;

    @Schema(description = "前端更新明细", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("前端更新明细")
    private String frontDesc;

    @Schema(description = "前端更新明细", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("前端更新明细")
    private String serverDesc;

    @Schema(description = "发布更新明细", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("发布更新明细")
    private String publishDesc;

    @Schema(description = "是否显示", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("是否显示")
    private Integer enabled;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}