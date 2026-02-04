package com.syj.eplus.module.wms.controller.admin.stockimport.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 仓储管理-库存明细导入 Response VO")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = false)
@ExcelIgnoreUnannotated
public class StockImportExcelVO {

    @ExcelProperty(value = "归属公司名称", index = 0)
    private String companyName;

    @ExcelProperty(value = "基础产品编号", index = 1)
    private String basicSkuCode;


    @ExcelProperty(value = "客户编码", index = 2)
    private String custCode;

    @ExcelProperty(value = "客户货号", index = 3)
    private String cskuCode;


    @ExcelProperty(value = "SKU编号(优先)", index = 4)
    private String skuCode;

    @ExcelProperty(value = "供应商编码", index = 5)
    private String venderCode;

    @ExcelProperty(value = "供应商名称", index = 6)
    private String venderName;
    @ExcelProperty(value = "采购合同号", index = 7)
    private String purchaseContractCode;
    @ExcelProperty(value = "客户PO号", index = 8)
    private String custPo;

    @ExcelProperty(value = "采购员", index = 9)
    private String purchaseUser;

    @ExcelProperty(value = "入库数量", index = 10)
    private Integer initQuantity;

    @ExcelProperty(value = "价格", index = 11)
    private BigDecimal price;
    @ExcelProperty(value = "币种(默认RMB)", index = 12)
    private String currency;
    @ExcelProperty(value = "仓库名称", index = 13)
    private String warehouseName;

    @ExcelProperty(value = "存放位置", index = 14)
    private String position;

    @ExcelProperty(value = "外箱规格长度 cm", index = 15)
    private BigDecimal outerboxLength;

    @ExcelProperty(value = "外箱规格宽度 cm", index = 16)
    private BigDecimal outerboxWidth;

    @ExcelProperty(value = "外箱规格高度 cm", index = 17)
    private BigDecimal outerboxHeight;

    @ExcelProperty(value = "单箱毛重 g", index = 18)
    private String outerboxGrossweight;

    @ExcelProperty(value = "备注", index = 19)
    private String remark;
    @ExcelProperty(value = "是否自营", index = 20)
    private String ownBrandFlag;
}