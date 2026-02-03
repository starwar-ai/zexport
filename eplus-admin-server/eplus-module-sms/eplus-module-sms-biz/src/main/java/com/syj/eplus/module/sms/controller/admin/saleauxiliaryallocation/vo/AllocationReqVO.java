package com.syj.eplus.module.sms.controller.admin.saleauxiliaryallocation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 销售合同辅料分摊新增/修改 Request VO")
@Data
public class AllocationReqVO {

    @Schema(description = "销售合同主键", example = "2010")
    private Long saleContractId;



    @Schema(description = "辅料采购合同明细主键", example = "11334")
    private Long auxiliaryPurchaseContractItemId;


     List<AllocationItemReqVO> list;
}