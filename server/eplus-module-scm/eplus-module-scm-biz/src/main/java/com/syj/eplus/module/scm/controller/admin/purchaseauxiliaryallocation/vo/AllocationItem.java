package com.syj.eplus.module.scm.controller.admin.purchaseauxiliaryallocation.vo;

import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 采购合同辅料分摊新增/修改 Request VO")
@Data
public class AllocationItem {

    @Schema(description = "采购合同明细id")
    private Long purchaseContractItemId;

    @Schema(description = "分摊金额")
    private JsonAmount allocationAmount;

}