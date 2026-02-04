package com.syj.eplus.module.scm.controller.admin.purchasecontract.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/09/21/15:15
 * @Description:
 */
@Data
public class RelatedPurchaseContractRespVO {

    @Schema(description = "销售合同")
    private Long saleContractNum;

    @Schema(description = "采购计划")
    private Long purchasePlanNum;

    @Schema(description = "采购变更单")
    private Long purchaseChangeNum;

    @Schema(description = "入库单")
    private Long receiptOrderNum;

    @Schema(description = "验货单")
    private Long inspectionOrderNum;

    @Schema(description = "付款单")
    private Long paymentOrderNum;

    @Schema(description = "退货单")
    private Long exchangeOrderNum;

    @Schema(description = "辅料合同")
    private Long auxiliaryContractNum;
}
