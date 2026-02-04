package com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 采购计划明细新增/修改 Request VO")
@Data
public class PurchasePlanItemToContractSaveReqVO extends  PurchasePlanItemRespVO{

   @Schema(description = "关联ID")
    private Long sourceId;

    @Schema(description = "主体ID")
    private Long companyId;

    @Schema(description = "主体名称")
    private String companyName;

 @Schema(description = "产品删除标记")
 private Integer skuDeletedFlag;

}
