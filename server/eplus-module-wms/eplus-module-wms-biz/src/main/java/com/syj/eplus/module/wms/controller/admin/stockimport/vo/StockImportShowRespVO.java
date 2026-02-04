package com.syj.eplus.module.wms.controller.admin.stockimport.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 仓储管理-库存明细导入 Response VO")
@Data
@ExcelIgnoreUnannotated
public class StockImportShowRespVO {

    @Schema(description = "导入编号")
    private String importCode;
    @Schema(description = "成功列表")
    private List<StockImportRespVO> successList;

    @Schema(description = "失败列表")
    private List<StockImportRespVO> errorList;
}