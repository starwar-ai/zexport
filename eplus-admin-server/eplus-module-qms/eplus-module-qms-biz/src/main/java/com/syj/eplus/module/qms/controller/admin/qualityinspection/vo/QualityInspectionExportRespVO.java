package com.syj.eplus.module.qms.controller.admin.qualityinspection.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 验货单 Response VO")
@Data
@ExcelIgnoreUnannotated
public class QualityInspectionExportRespVO {

    @Schema(description = "序号")
    @ExcelProperty("序号")
    @ColumnWidth(8)
    private Integer sortNumb;

    @Schema(description = "采购员部门", example = "芋艿")
    @ExcelProperty("部门")
    @ColumnWidth(12)
    private String purchaseDeptName;

    @Schema(description = "实际验货时间")
    @ExcelProperty("验货时间")
    @ColumnWidth(20)
    private LocalDateTime inspectionTime;

    @Schema(description = "采购合同号")
    @ExcelProperty("订单号")
    @ColumnWidth(18)
    private String purchaseContractCode;

    @Schema(description = "供应商名称")
    @ExcelProperty("工厂名称")
    @ColumnWidth(20)
    private String venderName;

    @Schema(description = "客户货号")
    @ExcelProperty("货号")
    @ColumnWidth(20)
    private String cskuCode;

    @Schema(description = "产品名称")
    @ExcelProperty("产品名称")
    @ColumnWidth(30)
    private String skuName;

    @Schema(description = "数量")
    @ExcelProperty("数量")
    @ColumnWidth(15)
    private String quantity;

    @Schema(description = "验货结论")
    @ExcelProperty("验货结论")
    @ColumnWidth(15)
    private String inspectionStatus;

    @Schema(description = "问题点")
    @ExcelProperty("问题点")
    @ColumnWidth(40)
    private String failDesc;

    @Schema(description = "验货员")
    @ExcelProperty("验货员")
    @ColumnWidth(12)
    private String inspectorName;

    @Schema(description = "采购员")
    @ExcelProperty("采购员")
    @ColumnWidth(12)
    private String buyerName;

    @Schema(description = "验货费用")
    @ExcelProperty("验货费用")
    @ColumnWidth(12)
    private String amount;

    @Schema(description = "备注")
    @ExcelProperty("备注")
    @ColumnWidth(20)
    private String remark;

}