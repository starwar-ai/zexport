package com.syj.eplus.module.dms.controller.admin.shipment.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/09/24/11:36
 * @Description:
 */
@Data
public class RelatedShipmentRespVO {

    @Schema(description = "销售合同")
    private Long SaleContractNum;

    @Schema(description = "采购合同")
    private Long purchaseContractNum;

    @Schema(description = "出运计划")
    private Long shipmentPlanNum;

    @Schema(description = "拉柜通知单")
    private Long noticeOrderNum;

    @Schema(description = "报关单")
    private Long declarationNum;

    @Schema(description = "结汇单")
    private Long settlementOrderNum;

    @Schema(description = "商检单")
    private Long commodityInspectionNum;
}
