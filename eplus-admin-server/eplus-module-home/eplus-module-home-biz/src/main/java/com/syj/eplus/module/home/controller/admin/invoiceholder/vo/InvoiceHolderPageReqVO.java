package com.syj.eplus.module.home.controller.admin.invoiceholder.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 发票夹分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class InvoiceHolderPageReqVO extends PageParam {

    @Schema(description = "报销类型", example = "1")
    private Integer reimbType;

    @Schema(description = "发票金额")
    private BigDecimal invoiceAmount;

    @Schema(description = "报销金额")
    private BigDecimal reimbAmount;

    @Schema(description = "报销事项")
    private String reimbItem;

    @Schema(description = "报销凭证")
    private String invoice;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "录入人")
    private String inputUser;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "id列表", example = "[1024,2048]")
    private List<Long> idList;

}