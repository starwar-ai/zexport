package com.syj.eplus.module.oa.controller.admin.travelapp.vo;

import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 出差申请单新增/修改 Request VO")
@Data
public class TravelAppSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "5613")
    private Long id;

    @Schema(description = "企微申请单id", example = "12382")
    private String wecomId;

    @Schema(description = "申请人", example = "13690")
    private Long applyerId;

    @Schema(description = "申请时间")
    private LocalDateTime applyTime;

    @Schema(description = "出差事由")
    private String purpose;

    @Schema(description = "出差地点")
    private String dest;

    @Schema(description = "审核状态", example = "2")
    private Integer auditStatus;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "出差时长(秒)")
    private Long duration;

    @Schema(description = "交通工具", example = "1")
    private Integer transportationType;

    @Schema(description = "同行人员")
    private List<UserDept> companions;

    @Schema(description = "附件")
    private List<SimpleFile> annex;

    @Schema(description = "拟达成目标")
    private String intendedObjectives;
}