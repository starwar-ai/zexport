package com.syj.eplus.module.sms.controller.admin.salecontract.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/09/23/9:08
 * @Description:
 */
@Data
public class RelatedSaleContractRespVO {

    @Schema(description = "销售变更单")
    private Long saleContractChangeNum;

    @Schema(description = "采购计划")
    private Long purchasePlanNum;

    @Schema(description = "采购合同")
    private Long purchaseContractNum;

    @Schema(description = "出运计划")
    private Long shipmentPlanNum;

    @Schema(description = "出运明细")
    private Long shipmentNum;

    @Schema(description = "收款账单")
    private Long collectionNum;

    @Schema(description = "辅料采购合同")
    private Long auxiliaryPurchaseContractNum;
}
