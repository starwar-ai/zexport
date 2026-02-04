package com.syj.eplus.module.oa.controller.admin.feeshareitem.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 费用归集明细分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FeeShareItemPageReqVO extends PageParam {

    @Schema(description = "费用归属id", example = "29806")
    private Long shareFeeId;

    @Schema(description = "费用归属对象类型", example = "1")
    private Integer businessSubjectType;

    @Schema(description = "费用归属对象编号")
    private Long businessSubjectCode;

    @Schema(description = "分摊比例")
    private Integer ratio;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}