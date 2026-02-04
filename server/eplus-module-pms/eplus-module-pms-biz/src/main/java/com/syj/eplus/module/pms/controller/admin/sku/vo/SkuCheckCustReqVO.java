package com.syj.eplus.module.pms.controller.admin.sku.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class SkuCheckCustReqVO {

    @Schema(description = "客户编号")
    private String custCode;

    @Schema(description = "产品列表")
    private List<Long> skuIdList;
}
