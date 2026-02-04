package com.syj.eplus.module.scm.api.invoicingnotices.dto;

import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class InvoicingNoticesDTO {
    @Schema(description = "归属公司主键", example = "12936")
    private Long companyId;

    @Schema(description = "归属公司名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @NotEmpty(message = "归属公司名称不能为空")
    private String companyName;

    @Schema(description = "供应商编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "供应商编号不能为空")
    private String venderCode;

    @Schema(description = "供应商名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotEmpty(message = "供应商名称不能为空")
    private String venderName;

    @Schema(description = "跟单员", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "跟单员不能为空")
    private UserDept manager;

    @Schema(description = "出运单号")
    private String shipmentCode;

    @Schema(description = "出运发票号")
    private String shipInvoiceCode;

    @Schema(description = "出运日期")
    private LocalDateTime shipDate;

    @Schema(description = "打印状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "打印状态不能为空")
    private Integer printFlag;

    @Schema(description = "采购单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "采购单号不能为空")
    private String purOrderCode;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "是否手动生成", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否手动生成不能为空")
    private Integer manuallyFlag;

    @Schema(description = "提交审核标识")
    private Integer submitFlag;

    @Schema(description = "开票通知明细")
    private List<InvoicingNoticesItemDTO> children;

    @Schema(description = "录入人")
    private UserDept inputUser;

    @Schema(description = "录入日期")
    private LocalDateTime inputDate;

    @Schema(description = "开票来源")
    private Integer sourceType;

    @Schema(description = "链路编号")
    private List<String> linkCodeList;
}
