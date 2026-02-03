package com.syj.eplus.module.dms.controller.admin.shipmentplan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/09/23/14:44
 * @Description:
 */
@Data
public class RelatedShipmentPlanRespVO {
    @Schema(description = "销售合同")
    private Long saleContractNum;

    @Schema(description = "采购合同")
    private Long purchaseContractNum;

    @Schema(description = "出运明细")
    private Long shipmentNum;
}
