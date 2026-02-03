package com.syj.eplus.module.oa.controller.admin.travelapp.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 出差申请单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TravelAppPageReqVO extends PageParam {

    @Schema(description = "企微申请单id", example = "12382")
    private String wecomId;

    @Schema(description = "出差申请单编号", example = "12382")
    private String code;

    @Schema(description = "申请人", example = "13690")
    private Long applyerId;

    @Schema(description = "申请时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] applyTime;

    @Schema(description = "出差事由")
    private String purpose;

    @Schema(description = "出差地点")
    private String dest;

    @Schema(description = "审核状态", example = "2")
    private Integer auditStatus;

    @Schema(description = "开始时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] startTime;

    @Schema(description = "结束时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] endTime;

    @Schema(description = "出差时长(秒)")
    private Long duration;

    @Schema(description = "交通工具", example = "1")
    private Integer transportationType;

    @Schema(description = "同行人员")
    private String companions;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}