package com.syj.eplus.module.dms.controller.admin.shipment.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
@Data
public class ToSettlementFormReqVO {
    @Schema(description = "下单主体", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long companyId;
    private List<SimpleSettlementFormReqVO> settlementFormList;
}