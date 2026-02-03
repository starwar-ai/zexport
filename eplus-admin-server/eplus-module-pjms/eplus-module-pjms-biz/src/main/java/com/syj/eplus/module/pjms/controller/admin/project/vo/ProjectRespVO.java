package com.syj.eplus.module.pjms.controller.admin.project.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
@Schema(description = "管理后台 - 项目 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ProjectRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "31010")
    @ExcelProperty("主键")
    private Long id;
    
    @Schema(description = "编号")
    @ExcelProperty("编号")
    private String code;
    
    @Schema(description = "名称", example = "芋艿")
    @ExcelProperty("名称")
    private String name;
    
    @Schema(description = "项目状态", example = "2")
    @ExcelProperty("项目状态")
    private Integer projectStatus;
    
    @Schema(description = "审核状态", example = "1")
    @ExcelProperty("审核状态")
    private Integer auditStatus;
    
    @Schema(description = "主体id", example = "18858")
    @ExcelProperty("主体id")
    private Long companyId;
    
    @Schema(description = "主体名称", example = "芋艿")
    @ExcelProperty("主体名称")
    private String companyName;
    
    @Schema(description = "研发类型", example = "2")
    @ExcelProperty("研发类型")
    private Integer developType;
    
    @Schema(description = "计划开始日期")
    @ExcelProperty("计划开始日期")
    private LocalDateTime planStartDate;
    
    @Schema(description = "计划结束日期")
    @ExcelProperty("计划结束日期")
    private LocalDateTime planEndDate;
    
    @Schema(description = "实际开始日期")
    @ExcelProperty("实际开始日期")
    private LocalDateTime startDate;
    
    @Schema(description = "实际结束日期")
    @ExcelProperty("实际结束日期")
    private LocalDateTime endDate;
    
    @Schema(description = "负责人id", example = "16397")
    @ExcelProperty("负责人id")
    private Long ownerUserId;
    
    @Schema(description = "负责人姓名", example = "王五")
    @ExcelProperty("负责人姓名")
    private String ownerUserName;
    
    @Schema(description = "负责人部门id", example = "26523")
    @ExcelProperty("负责人部门id")
    private Long ownerDeptId;
    
    @Schema(description = "负责人部门名称", example = "赵六")
    @ExcelProperty("负责人部门名称")
    private String ownerDeptName;
    
    @Schema(description = "项目预算")
    @ExcelProperty("项目预算")
    private JsonAmount budget;
    
    @Schema(description = "申请日期")
    @ExcelProperty("申请日期")
    private LocalDateTime applyDate;
    
    @Schema(description = "申请人id", example = "27802")
    @ExcelProperty("申请人id")
    private Long applyUserId;
    
    @Schema(description = "申请人姓名", example = "王五")
    @ExcelProperty("申请人姓名")
    private String applyUserName;
    
    @Schema(description = "申请人部门id", example = "1486")
    @ExcelProperty("申请人部门id")
    private Long applyDeptId;
    
    @Schema(description = "申请人部门名称", example = "王五")
    @ExcelProperty("申请人部门名称")
    private String applyDeptName;
    
    @Schema(description = "备注", example = "你猜")
    @ExcelProperty("备注")
    private String remark;
    
    @Schema(description = "完成时间")
    @ExcelProperty("完成时间")
    private LocalDateTime doneTime;
    
    @Schema(description = "结案时间")
    @ExcelProperty("结案时间")
    private LocalDateTime finishTime;
    
    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "计划日期")
    @ExcelProperty("计划日期")
    private LocalDateTime[] planDate;


    @Schema(description = "任务id")
    private String processInstanceId;
    
}