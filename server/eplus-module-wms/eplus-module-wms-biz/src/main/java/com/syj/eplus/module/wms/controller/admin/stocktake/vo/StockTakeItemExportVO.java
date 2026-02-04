
package com.syj.eplus.module.wms.controller.admin.stocktake.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.metadata.data.WriteCellData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@ExcelIgnoreUnannotated
@HeadFontStyle(fontName = "宋体",fontHeightInPoints = 11)
public class StockTakeItemExportVO {
    @ExcelProperty(value = "Vertak Product Image")
    private WriteCellData<Void> cptp;

    @ExcelProperty(value ="品名")
    private String skuName;

    @ExcelProperty(value ="客户货号")
    private String cskuCode;

    @ExcelProperty(value ="客户PO号")
    private String custPo;

    @ExcelProperty(value ="数量")
    private Integer stocktakeStockQuantity;

    @ExcelProperty(value ="箱数")
    private Integer stocktakeStockBoxQuantity;

    @ExcelProperty(value ="外箱装量")
    private Integer qtyPerOuterbox;

    @ExcelProperty(value ="备注")
    private String remark;

    @ExcelProperty(value ="差异原因")
    private String diffReasons;

    @Schema(description = "入库时间")
    private String receiptTime;

}