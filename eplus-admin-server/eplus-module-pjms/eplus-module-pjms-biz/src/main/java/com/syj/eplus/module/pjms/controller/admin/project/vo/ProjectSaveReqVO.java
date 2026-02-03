package com.syj.eplus.module.pjms.controller.admin.project.vo;

import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 项目新增/修改 Request VO")
@Data
public class ProjectSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "31010")
    private Long id;

    @Schema(description = "编号")
    private String code;

    @Schema(description = "名称", example = "芋艿")
    private String name;

    @Schema(description = "项目状态", example = "2")
    private Integer projectStatus;

    @Schema(description = "审核状态", example = "1")
    private Integer auditStatus;

    @Schema(description = "主体id", example = "18858")
    private Long companyId;

    @Schema(description = "主体名称", example = "芋艿")
    private String companyName;

    @Schema(description = "研发类型", example = "2")
    private Integer developType;

    @Schema(description = "计划开始日期")
    private LocalDateTime planStartDate;

    @Schema(description = "计划结束日期")
    private LocalDateTime planEndDate;

    @Schema(description = "实际开始日期")
    private LocalDateTime startDate;

    @Schema(description = "实际结束日期")
    private LocalDateTime endDate;

    @Schema(description = "负责人id", example = "16397")
    private Long ownerUserId;

    @Schema(description = "负责人姓名", example = "王五")
    private String ownerUserName;

    @Schema(description = "负责人部门id", example = "26523")
    private Long ownerDeptId;

    @Schema(description = "负责人部门名称", example = "赵六")
    private String ownerDeptName;

    @Schema(description = "项目预算")
    private JsonAmount budget;

    @Schema(description = "申请日期")
    private LocalDateTime applyDate;

    @Schema(description = "申请人id", example = "27802")
    private Long applyUserId;

    @Schema(description = "申请人姓名", example = "王五")
    private String applyUserName;

    @Schema(description = "申请人部门id", example = "1486")
    private Long applyDeptId;

    @Schema(description = "申请人部门名称", example = "王五")
    private String applyDeptName;

    @Schema(description = "备注", example = "你猜")
    private String remark;

    @Schema(description = "完成时间")
    private LocalDateTime doneTime;

    @Schema(description = "结案时间")
    private LocalDateTime finishTime;

    @Schema(description = "提交审核标识")
    private Integer submitFlag;


    //前端传参
    @Schema(description = "计划日期")
    private LocalDateTime[] planDate;
}