package com.syj.eplus.module.home.controller.admin.invoiceholder.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 发票夹 Response VO")
@Data
@ExcelIgnoreUnannotated
public class InvoiceHolderRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "27245")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "报销类型", example = "1")
    @ExcelProperty("报销类型")
    private Integer reimbType;

    @Schema(description = "发票金额")
    @ExcelProperty("发票金额")
    private BigDecimal invoiceAmount;

    @Schema(description = "报销金额")
    @ExcelProperty("报销金额")
    private BigDecimal reimbAmount;

    @Schema(description = "报销事项", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("报销事项")
    private String reimbItem;

    @Schema(description = "报销凭证", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("报销凭证")
    private List<SimpleFile> invoice;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("状态")
    private Integer status;

    @Schema(description = "录入人", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("录入人")
    private UserDept inputUser;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "费用说明")
    private String description;

    @Schema(description = "发票号")
    private String invoiceCode;

    @Schema(description = "发票类型")
    private String invoiceType;

    @Schema(description = "发票名称")
    private String invoiceName;

    @Schema(description = "发票科目")
    private Long dictSubjectId;
}