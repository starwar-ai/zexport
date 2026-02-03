package com.syj.eplus.module.oa.controller.admin.repayapp.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 还款单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RepayAppPageReqVO extends PageParam {

    @Schema(description = "借款申请单id", example = "24122")
    private Long loanAppId;

    @Schema(description = "借款申请单编号", example = "24122")
    private String loanAppCode;

    @Schema(description = "申请单号")
    private String code;

    @Schema(description = "打印状态")
    private Integer printFlag;

    @Schema(description = "打印次数")
    private Integer printTimes;

    @Schema(description = "审核状态", example = "1")
    private Integer auditStatus;

    @Schema(description = "还款金额")
    private JsonAmount amount;

    @Schema(description = "备注", example = "你说的对")
    private String remark;

    @Schema(description = "还款时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] repayTime;

    @Schema(description = "还款人id", example = "2389")
    private Long repayerId;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "还款状态")
    private Integer repayStatus;
}