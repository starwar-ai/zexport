package com.syj.eplus.module.fms.controller.admin.bankregistration.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 银行登记分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BankRegistrationPageReqVO extends PageParam {

    @Schema(description = "编号")
    private String code;

    @Schema(description = "公司名称")
    private String companyName;

    @Schema(description = "公司抬头")
    private String companyTitle;

    @Schema(description = "客户名称", example = "王五")
    private String custName;

    @Schema(description = "认领状态", example = "1")
    private Integer claimStatus;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "入账金额")
    private String amount;

    @Schema(description = "业务员")
    private Long managerId;

    @Schema(description = "收款银行")
    private String bank;

}