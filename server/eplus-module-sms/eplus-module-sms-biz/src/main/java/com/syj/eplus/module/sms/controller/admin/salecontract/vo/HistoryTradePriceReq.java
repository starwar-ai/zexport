package com.syj.eplus.module.sms.controller.admin.salecontract.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/10 17:34
 */
@Schema(description = "管理后台 - 历史成交价 Request VO")
@Data
public class HistoryTradePriceReq {

    @Schema(description = "产品编号")
    private String skuCode;

    @Schema(description = "销售时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] saleTime;
}
