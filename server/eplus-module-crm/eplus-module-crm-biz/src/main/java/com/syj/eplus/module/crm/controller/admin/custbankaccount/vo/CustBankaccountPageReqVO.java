package com.syj.eplus.module.crm.controller.admin.custbankaccount.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 银行账户信息分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CustBankaccountPageReqVO extends PageParam {

    @Schema(description = "版本号")
    private Integer ver;

    @Schema(description = "客户id", example = "24817")
    private Integer custId;

    @Schema(description = "开户行")
    private String bank;

    @Schema(description = "银行账户", example = "4349")
    private String bankAccount;

    @Schema(description = "是否默认账户0-否，1-是")
    private Integer defaultFlag;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "开户行地址")
    private String bankAddress;

    @Schema(description = "开户行联系人")
    private String bankPoc;

    @Schema(description = "银行账号")
    private String bankCode;
}