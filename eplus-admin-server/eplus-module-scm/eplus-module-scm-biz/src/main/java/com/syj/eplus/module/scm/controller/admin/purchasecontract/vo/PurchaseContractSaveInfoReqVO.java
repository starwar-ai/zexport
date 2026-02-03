package com.syj.eplus.module.scm.controller.admin.purchasecontract.vo;

import com.syj.eplus.module.scm.controller.admin.purchasecontractitem.vo.PurchaseContractItemSaveReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 采购合同新增/修改 Request VO")
@Data
public class PurchaseContractSaveInfoReqVO extends   PurchaseContractSaveReqVO{

    @Schema(description = "采购计划明细列表")
    private List<PurchaseContractItemSaveReqVO> children;

    private Integer submitFlag;

}