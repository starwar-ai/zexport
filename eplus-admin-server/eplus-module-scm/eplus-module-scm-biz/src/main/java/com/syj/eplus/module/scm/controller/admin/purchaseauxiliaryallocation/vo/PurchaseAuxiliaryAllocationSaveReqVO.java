package com.syj.eplus.module.scm.controller.admin.purchaseauxiliaryallocation.vo;

import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Schema(description = "管理后台 - 采购合同辅料分摊新增/修改 Request VO")
@Data
public class PurchaseAuxiliaryAllocationSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "7679")
    private Long id;

    @Schema(description = "采购合同主键", example = "21266")
    private Long purchaseContractId;

    @Schema(description = "采购合同编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "采购合同编号不能为空")
    private String purchaseContractCode;

    @Schema(description = "采购合同明细主键", example = "20282")
    private Long purchaseContractItemId;

    @Schema(description = "产品编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "产品编号不能为空")
    private String skuCode;

    @Schema(description = "产品名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @NotEmpty(message = "产品名称不能为空")
    private String skuName;

    @Schema(description = "客户货号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "客户货号不能为空")
    private String cskuCode;

    @Schema(description = "辅料采购合同主键", example = "18880")
    private Long auxiliaryPurchaseContractId;

    @Schema(description = "辅料采购合同编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "辅料采购合同编号不能为空")
    private String auxiliaryPurchaseContractCode;

    @Schema(description = "辅料采购合同明细主键", example = "9513")
    private Long auxiliaryPurchaseContractItemId;

    @Schema(description = "辅料产品编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "辅料产品编号不能为空")
    private String auxiliarySkuCode;

    @Schema(description = "辅料产品名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @NotEmpty(message = "辅料产品名称不能为空")
    private String auxiliarySkuName;

    @Schema(description = "采购数量", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "采购数量不能为空")
    private String quantity;

    @Schema(description = "分摊金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "分摊金额不能为空")
    private JsonAmount allocationAmount;

    @Schema(description = "提交审核标识")
    private Integer submitFlag;
}