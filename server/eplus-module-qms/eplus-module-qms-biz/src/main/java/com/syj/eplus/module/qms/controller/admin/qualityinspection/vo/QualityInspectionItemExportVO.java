package com.syj.eplus.module.qms.controller.admin.qualityinspection.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.metadata.data.WriteCellData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@ExcelIgnoreUnannotated
@HeadFontStyle(fontName = "宋体",fontHeightInPoints = 11)
public class QualityInspectionItemExportVO {

    @Schema(description = "产品中文名称")
    @ExcelProperty("产品中文名称")
    private String skuName;

    @Schema(description = "产品编号")
    @ExcelProperty("产品编号")
    private String skuCode;

    @Schema(description = "客户货号")
    @ExcelProperty("客户货号")
    private String cskuCode;

    @Schema(description = "订单数量")
    @ExcelProperty("订单数量")
    private Integer quantity;

    @Schema(description = "外箱装量")
    @ExcelProperty("外箱装量")
    private Integer qtyPerOuterbox;

    @Schema(description = "箱数")
    @ExcelProperty("箱数")
    private Integer boxCount;

    @Schema(description = "包装方式名称")
    @ExcelProperty("包装方式名称")
    private String packageTypeName;

    @Schema(description = "产品描述")
    @ExcelProperty("产品描述")
    private String description;

    @ExcelProperty(value = "Vertak Product Image")
    private WriteCellData<Void> cptp;
}