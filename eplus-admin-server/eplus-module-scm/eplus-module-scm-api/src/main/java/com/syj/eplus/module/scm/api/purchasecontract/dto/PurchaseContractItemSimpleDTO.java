package com.syj.eplus.module.scm.api.purchasecontract.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PurchaseContractItemSimpleDTO {

    private Long id;

    @Schema(description = "序号")
    private Integer sortNum;

    @Schema(description = "产品id")
    private Long skuId;

    @Schema(description = "产品编号")
    private String skuCode;

    @Schema(description = "产品名称")
    private String skuName;

    @Schema(description = "客户货号")
    private String cskuCode;

    @Schema(description = "采购数量")
    private Integer quantity;

    @Schema(description = "是否客户产品标记")
    private Integer custProFlag;
}
