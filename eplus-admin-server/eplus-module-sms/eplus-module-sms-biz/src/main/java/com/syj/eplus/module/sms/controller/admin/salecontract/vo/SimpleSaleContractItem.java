package com.syj.eplus.module.sms.controller.admin.salecontract.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "销售合同-关联单据明细")
public class SimpleSaleContractItem {

    @Schema(description = "产品编号")
    private String skuCode;

    @Schema(description = "产品中文名称")
    private String skuName;

    @Schema(description = "产品英文名称")
    private String skuNameEng;

    @Schema(description = "销售数量")
    private Integer quantity;

    @Schema(description = "锁定数量")
    private Integer stockLockQuantity;

    @Schema(description = "应采数量")
    private Integer purchaseQuantity;
}
