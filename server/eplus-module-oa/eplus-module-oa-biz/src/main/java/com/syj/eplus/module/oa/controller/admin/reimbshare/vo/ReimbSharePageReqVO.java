package com.syj.eplus.module.oa.controller.admin.reimbshare.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 报销分摊分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ReimbSharePageReqVO extends PageParam {

    @Schema(description = "报销单编号", example = "28790")
    private Long reimbId;

    @Schema(description = "费用归属类型", example = "1")
    private Integer auxiliaryType;

    @Schema(description = "费用归属对象id", example = "18626")
    private Long auxiliaryId;

    @Schema(description = "分摊比例")
    private BigDecimal ratio;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}