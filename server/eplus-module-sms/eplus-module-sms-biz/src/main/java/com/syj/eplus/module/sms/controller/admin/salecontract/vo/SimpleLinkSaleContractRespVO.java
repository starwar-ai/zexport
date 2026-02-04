package com.syj.eplus.module.sms.controller.admin.salecontract.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "管理后台 - 外销合同-关联单据信息")
public class SimpleLinkSaleContractRespVO {

    @Schema(description = "销售合同编号")
    private String code;

    @Schema(description = "下单主体")
    private String companyName;

    @Schema(description = "客户名称")
    private String custName;

    @Schema(description = "明细")
    List<SimpleSaleContractItem> children;
}
