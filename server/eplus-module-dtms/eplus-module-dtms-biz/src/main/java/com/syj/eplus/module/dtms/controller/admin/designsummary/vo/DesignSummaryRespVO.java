package com.syj.eplus.module.dtms.controller.admin.designsummary.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
@Schema(description = "管理后台 - 设计-工作总结 Response VO")
@Data
@ExcelIgnoreUnannotated
public class DesignSummaryRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "31317")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "设计任务单主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "17656")
    @ExcelProperty("设计任务单主键")
    private Long designId;

    @Schema(description = "设计任务单-明细主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "10285")
    @ExcelProperty("设计任务单-明细主键")
    private Long designItemId;

    @Schema(description = "认领人主键", example = "20292")
    @ExcelProperty("认领人主键")
    private Long designerId;

    @Schema(description = "认领人姓名", example = "张三")
    @ExcelProperty("认领人姓名")
    private String designerName;

    @Schema(description = "认领人部门主键", example = "24824")
    @ExcelProperty("认领人部门主键")
    private Long designerDeptId;

    @Schema(description = "认领人部门名称", example = "王五")
    @ExcelProperty("认领人部门名称")
    private String designerDeptName;

    @Schema(description = "当前进度")
    @ExcelProperty("当前进度")
    private String progress;

    @Schema(description = "进度描述")
    @ExcelProperty("进度描述")
    private String progressDesc;

    @Schema(description = "备注", example = "你猜")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
