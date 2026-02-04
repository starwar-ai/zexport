package com.syj.eplus.module.dtms.controller.admin.designsummary.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 设计-工作总结分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DesignSummaryPageReqVO extends PageParam {

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

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
