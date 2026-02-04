package com.syj.eplus.module.pms.controller.admin.hsdata.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 海关编码分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class HsdataPageReqVO extends PageParam {

    @Schema(description = "编码")
    private String code;

    @Schema(description = "商品名称")
    private String name;

    @Schema(description = "报关单位")
    private String unit;

    @Schema(description = "退税率")
    private BigDecimal taxRefundRate;

    @Schema(description = "征税率")
    private BigDecimal rate;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "商品全称")
    private String chname;

    @Schema(description = "征收率")
    private BigDecimal addrate;

    @Schema(description = "第二单位")
    private String code2;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}