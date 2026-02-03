package com.syj.eplus.module.oa.controller.admin.reimb.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/21/15:08
 * @Description:
 */
@Data
@Schema(description = "报销归集分页查询条件")
public class ReimbAuxiliaryPageReqVO {
    @Schema(description = "归集状态")
    private Integer auxiliaryType;

    @Schema(description = "报销主体")
    private Long companyId;

    @Schema(description = "报销类型")
    private Integer reimbType;

    @Schema(description = "费用说明")
    private String description;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;


}
