package com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.syj.eplus.module.scm.controller.admin.purchaseplan.vo.PurchasePlanInfoRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 采购计划明细 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PurchasePlanItemAndPlanRespVO extends PurchasePlanItemRespVO {

    @Schema(description = "采购计划链接主表")
    private PurchasePlanInfoRespVO purchasePlan;
}