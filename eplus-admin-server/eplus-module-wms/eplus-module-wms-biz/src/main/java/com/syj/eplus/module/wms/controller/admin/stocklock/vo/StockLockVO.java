package com.syj.eplus.module.wms.controller.admin.stocklock.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 仓储管理-库存明细-锁定库存 Response VO")
@Data
@ExcelIgnoreUnannotated
public class StockLockVO {

//    @Schema(description = "原单据主键")
//    @ExcelProperty("原单据主键")
//    private String sourceOrderId;

//    @Schema(description = "原单据单号")
//    @ExcelProperty("原单据单号")
//    private String sourceOrderCode;

    @Schema(description = "产品库存主键", example = "8212")
    @ExcelProperty("产品库存主键")
    private Long stockId;

    @Schema(description = "批次号")
    @ExcelProperty("批次号")
    private String batchCode;

    @Schema(description = "产品sku主键", example = "8212")
    @ExcelProperty("产品sku主键")
    private Long skuId;

    @Schema(description = "占用数量")
    @ExcelProperty("占用数量")
    private Integer lockQuantity;

}
