package com.syj.eplus.module.home.controller.admin.invoiceholder.vo;

import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 发票夹新增/修改 Request VO")
@Data
public class InvoiceHolderSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "27245")
    private Long id;

    @Schema(description = "报销类型", example = "1")
    private Integer reimbType;

    @Schema(description = "发票金额")
    private BigDecimal invoiceAmount;

    @Schema(description = "报销金额")
    private BigDecimal reimbAmount;

    @Schema(description = "报销事项", requiredMode = Schema.RequiredMode.REQUIRED)
    private String reimbItem;

    @Schema(description = "报销凭证", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<SimpleFile> invoice;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    @Schema(description = "录入人", requiredMode = Schema.RequiredMode.REQUIRED)
    private UserDept inputUser;

    @Schema(description = "费用说明")
    private String description;

    @Schema(description = "发票号")
    private String invoiceCode;

    @Schema(description = "发票类型")
    private String invoiceType;

    @Schema(description = "类别主键")
    private Long dictSubjectId;

    @Schema(description = "发票日期")
    private LocalDateTime invoiceDate;
}