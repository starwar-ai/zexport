package com.syj.eplus.module.scm.api.purchasecontract.dto;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PurchaseContractGetItemPageReqDTO extends PageParam {

    @Schema(description = "客户货号")
    private  String CskuCode;

    @Schema(description = "采购合同")
    private String purchaseContractCode;

    @Schema(description = "销售合同")
    private String saleContractCode;

    @Schema(description = "规格描述")
    private String specDesc;

    @Schema(description = "产品名称")
    private String skuName;

    @Schema(description = "供应商编号")
    private String venderName;

    @Schema(description = "供应商编号")
    private List<String> venderCodeList;

    @Schema(description = "辅助采购合同编号")
    private String auxiliaryPurchaseContractCode;

    @Schema(description = "辅助采购合同明细编号")
    private String skuCode;
}
