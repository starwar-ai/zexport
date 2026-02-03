package com.syj.eplus.module.scm.controller.admin.purchaseplan.vo;

import com.syj.eplus.module.scm.controller.admin.purchaseplanitem.vo.PurchasePlanItemSaveReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 采购计划新增/修改 Request VO")
@Data
public class PurchasePlanInfoSaveReqVO extends   PurchasePlanSaveReqVO {

    @Schema(description = "采购计划明细列表")
    private List<PurchasePlanItemSaveReqVO> children;

    private Integer submitFlag;

    @Schema(description = "是否标准采购")
    private Integer toContractFlag;



}