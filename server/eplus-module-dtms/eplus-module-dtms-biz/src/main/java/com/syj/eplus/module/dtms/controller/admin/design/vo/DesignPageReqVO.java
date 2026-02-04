package com.syj.eplus.module.dtms.controller.admin.design.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 设计-任务单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DesignPageReqVO extends PageParam {

    @Schema(description = "单号")
    private String code;

    @Schema(description = "设计任务名称", example = "芋艿")
    private String name;

    @Schema(description = "任务状态：1：待提交，2：待审批，3：待完成，4：待评价，5：已完成，6：已作废，7：已驳回", example = "1")
    private Integer designStatus;

    @Schema(description = "是否特批:0-否 1-是")
    private Integer specialPermissionFlag;

    @Schema(description = "合同编号")
    private String contractCode;

    @Schema(description = "申请人主键", example = "18329")
    private Long applyDesignerId;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "认领页标识:0-否 1-是")
    private Integer claimFlag;

    private List<Integer> designStatusList;

}
