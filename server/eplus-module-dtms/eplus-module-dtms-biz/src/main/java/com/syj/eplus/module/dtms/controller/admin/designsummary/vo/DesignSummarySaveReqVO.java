package com.syj.eplus.module.dtms.controller.admin.designsummary.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 设计-工作总结新增/修改 Request VO")
@Data
public class DesignSummarySaveReqVO {

    @Schema(description = "主键", example = "31317")
    private Long id;

    @Schema(description = "设计任务单主键", example = "17656")
    private Long designId;

    @Schema(description = "设计任务单-明细主键", example = "10285")
    private Long designItemId;

    @Schema(description = "认领人主键", example = "20292")
    private Long designerId;

    @Schema(description = "认领人姓名", example = "张三")
    private String designerName;

    @Schema(description = "认领人部门主键", example = "24824")
    private Long designerDeptId;

    @Schema(description = "认领人部门名称", example = "王五")
    private String designerDeptName;

    @Schema(description = "当前进度")
    private String progress;

    @Schema(description = "进度描述")
    private String progressDesc;

    @Schema(description = "备注", example = "你猜")
    private String remark;

}
