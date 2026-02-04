package com.syj.eplus.module.scm.controller.admin.purchaseauxiliaryallocation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 采购合同辅料分摊新增/修改 Request VO")
@Data
public class PurchaseAuxiliaryAllocationAllocationSaveReqVO {

    @Schema(description = "辅料采购合同明细id")
    private Long auxiliaryPurchaseContractItemId;

    @Schema(description = "采购合同明细分摊列表")
    private List<AllocationItem> allocationList;

}