package com.syj.eplus.module.dtms.controller.admin.designitem.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
@Schema(description = "管理后台 - 设计-认领明细 Response VO")
@Data
@ExcelIgnoreUnannotated
public class DesignItemRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "24411")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "设计任务单主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "9353")
    @ExcelProperty("设计任务单主键")
    private Long designId;

    @Schema(description = "认领人主键", example = "15376")
    @ExcelProperty("认领人主键")
    private Long designerId;

    @Schema(description = "认领人姓名", example = "赵六")
    @ExcelProperty("认领人姓名")
    private String designerName;

    @Schema(description = "认领人部门主键", example = "32204")
    @ExcelProperty("认领人部门主键")
    private Long designerDeptId;

    @Schema(description = "认领人部门名称", example = "张三")
    @ExcelProperty("认领人部门名称")
    private String designerDeptName;

    @Schema(description = "设计任务类型1：常规任务，2：临时任务", example = "2")
    @ExcelProperty("设计任务类型1：常规任务，2：临时任务")
    private Integer designType;

    @Schema(description = "设计文件位置")
    @ExcelProperty("设计文件位置")
    private String designFilePath;

    @Schema(description = "认领时间")
    @ExcelProperty("认领时间")
    private LocalDateTime acceptDate;

    @Schema(description = "计划开始时间")
    @ExcelProperty("计划开始时间")
    private LocalDateTime planStartDate;

    @Schema(description = "计划完成时间")
    @ExcelProperty("计划完成时间")
    private LocalDateTime planCompleteDate;

    @Schema(description = "完成时间")
    @ExcelProperty("完成时间")
    private LocalDateTime completeDate;

    @Schema(description = "评价结果；1：投诉，2：优秀，3：点赞")
    @ExcelProperty("评价结果；1：投诉，2：优秀，3：点赞")
    private Integer evaluateResult;

    @Schema(description = "评价描述")
    @ExcelProperty("评价描述")
    private String evaluateDesc;

    @Schema(description = "备注", example = "你猜")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
