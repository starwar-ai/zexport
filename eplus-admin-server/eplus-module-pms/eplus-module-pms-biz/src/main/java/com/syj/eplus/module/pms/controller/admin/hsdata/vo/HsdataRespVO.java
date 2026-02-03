package com.syj.eplus.module.pms.controller.admin.hsdata.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 海关编码 Response VO")
@Data
@ExcelIgnoreUnannotated
public class HsdataRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "版本号")
    @ExcelProperty("版本号")
    private String ver;

    @Schema(description = "编码")
    @ExcelProperty("编码")
    private String code;

    @Schema(description = "商品名称")
    @ExcelProperty("商品名称")
    private String name;

    @Schema(description = "报关单位")
    @ExcelProperty("报关单位")
    private String unit;

    @Schema(description = "退税率")
    @ExcelProperty("退税率")
    private BigDecimal taxRefundRate;

    @Schema(description = "征税率")
    @ExcelProperty("征税率")
    private BigDecimal rate;

    @Schema(description = "备注")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "商品全称")
    @ExcelProperty("商品全称")
    private String chname;

    @Schema(description = "征收率")
    @ExcelProperty("征收率")
    private BigDecimal addrate;

    @Schema(description = "第二单位")
    @ExcelProperty("第二单位")
    private String code2;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}