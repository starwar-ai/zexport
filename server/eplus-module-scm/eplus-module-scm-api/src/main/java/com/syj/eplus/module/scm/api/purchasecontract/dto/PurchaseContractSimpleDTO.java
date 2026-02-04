package com.syj.eplus.module.scm.api.purchasecontract.dto;

import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PurchaseContractSimpleDTO {

    private Long id;

    @Schema(description = "采购合同编号")
    private String code;

    @Schema(description = "采购主体id")
    private Long companyId;

    @Schema(description = "采购主体名称")
    private String companyName;

    @Schema(description = "销售合同id")
    private Long saleContractId;

    @Schema(description = "销售合同编号")
    private String saleContractCode;

    @Schema(description = "客户id")
    private Long custId;

    @Schema(description = "客户编号")
    private String custCode;

    @Schema(description = "客户名称")
    private String custName;


    @Schema(description = "供应商id")
    private Long venderId;

    @Schema(description = "供应商编号")
    private String venderCode;

    @Schema(description = "供应商名称")
    private String venderName;

    @Schema(description = "采购员id")
    private Long purchaseUserId;

    @Schema(description = "采购员名称")
    private String purchaseUserName;

    @Schema(description = "采购员部门id")
    private Long purchaseUserDeptId;

    @Schema(description = "采购员部门名称")
    private String purchaseUserDeptName;

    @Schema(description = "单据状态")
    private Integer contractStatus;

    @Schema(description = "采购总数量")
    private Integer totalQuantity;

    @Schema(description = "采购总数量")
    private JsonAmount totalAmount;

    @Schema(description = "产品列表")
    private List<PurchaseContractItemSimpleDTO> children;

    @Schema(description = "合同时间")
    private LocalDateTime purchaseTime;
}
