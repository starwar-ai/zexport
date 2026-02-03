package com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo;

import com.syj.eplus.module.scm.controller.admin.purchaseplan.vo.PurchasePlanInfoSaveReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 采购计划明细新增/修改 Request VO")
@Data
public class PurchasePlanItemToContractSaveInfoReqVO extends PurchasePlanInfoSaveReqVO {

   @Schema(description = "采购计划列表")
    private List<PurchasePlanItemToContractSaveReqVO> planList;

    @Schema(description = "组装单列表")
    private List<PurchasePlanItemToContractSaveReqVO> combineList;

}
