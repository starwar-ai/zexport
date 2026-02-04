package com.syj.eplus.module.pms.controller.admin.skubom.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 产品SKU BOM新增/修改 Request VO")
@Data
public class SkuBomSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "9993")
    private Long id;

    @Schema(description = "子SKUid", example = "22451")
    private Long skuId;
    @Schema(description = "子SKU编号", example = "22451")
    private Long skuCode;


    @Schema(description = "SKU版本")
    private Integer skuVer;

    @Schema(description = "数量")
    private Integer qty;

    @Schema(description = "父SKU编号", example = "27839")
    private Long parentSkuId;

    @Schema(description = "产品类型")
    private Integer skuType;
}