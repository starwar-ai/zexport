package com.syj.eplus.module.pms.controller.admin.hsdata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 海关编码新增/修改 Request VO")
@Data
public class HsdataSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "编码")
    private String code;

    @Schema(description = "版本号")
    private String ver;

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

}