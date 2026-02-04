package com.syj.eplus.module.oa.controller.admin.reimbrepaydetail.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 报销还款详情分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ReimbRepayDetailPageReqVO extends PageParam {

    @Schema(description = "报销单id", example = "29921")
    private Long reimbId;

    @Schema(description = "借款单id", example = "29108")
    private Long loanId;

    @Schema(description = "还款状态", example = "2")
    private Integer repayStatus;

    @Schema(description = "还款金额")
    private JsonAmount repayAmount;

    @Schema(description = "审核状态", example = "2")
    private Integer auditStatus;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}