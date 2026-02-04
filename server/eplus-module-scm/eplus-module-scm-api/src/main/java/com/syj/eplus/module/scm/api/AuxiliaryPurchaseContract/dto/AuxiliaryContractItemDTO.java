package com.syj.eplus.module.scm.api.AuxiliaryPurchaseContract.dto;

import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.module.sms.api.dto.SaleAuxiliaryAllocationDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class AuxiliaryContractItemDTO {


    @Schema(description = "辅料采购合同明细id")
    private Long contractItemId;

    @Schema(description = "辅料采购合同id")
    private Long contractId;

    @Schema(description = "辅料采购合同编号")
    private String contractCode;

    @Schema(description = "辅料采购合同中序号")
    private Integer sortNum;

    @Schema(description = "辅料id")
    private Long skuId;

    @Schema(description = "辅料code")
    private String skuCode;

    @Schema(description = "辅料名称")
    private String skuName;

    @Schema(description = "供应商id")
    private Long venderId;

    @Schema(description = "供应商编号")
    private String venderCode;

    @Schema(description = "供应商名称")
    private String venderName;

    @Schema(description = "辅料购买数量")
    private Integer quantity;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "辅料单价")
    private JsonAmount unitPrice;

//    @Schema(description = "辅料总金额")
//    private JsonAmount totalAmount;

    @Schema(description = "是否已分摊")
    private Integer allocationFlag;

    @Schema(description = "分摊列表")
    private List<SaleAuxiliaryAllocationDTO> allocationDTOList;
}
