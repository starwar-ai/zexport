package com.syj.eplus.module.pms.controller.admin.skubom.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 产品SKU BOM Response VO")
@Data
@ExcelIgnoreUnannotated
public class SkuBomRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "9993")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "子SKU编号", example = "22451")
    @ExcelProperty("子SKU编号")
    private Long skuId;

    @Schema(description = "SKU版本")
    @ExcelProperty("SKU版本")
    private Integer skuVer;

    @Schema(description = "数量")
    @ExcelProperty("数量")
    private Integer qty;

    @Schema(description = "父SKU编号", example = "27839")
    @ExcelProperty("父SKU编号")
    private Long parentSkuId;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "产品类型")
    private Integer skuType;
}