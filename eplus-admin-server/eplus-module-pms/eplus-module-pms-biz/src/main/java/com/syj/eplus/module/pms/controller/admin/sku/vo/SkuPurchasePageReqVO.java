package com.syj.eplus.module.pms.controller.admin.sku.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - sku分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SkuPurchasePageReqVO extends PageParam {

    @Schema(description = "客户货号")
    private  String CskuCode;

    @Schema(description = "采购合同编号")
    private  String purchaseContractCode;

    @Schema(description = "销售合同编号")
    private  String SaleContractCode;

    @Schema(description = "规格描述")
    private String desc;

}