package com.syj.eplus.module.scm.controller.admin.purchasecontract.vo;

import com.syj.eplus.framework.common.entity.SimpleFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class TransformAuxiliaryMidVO {

    @Schema(description = "采购合同主体id")
    private Long companyId;

    @Schema(description = "采购合同主体名称")
    private String companyName;

    @Schema(description = "采购合同明细id")
    private Long purchaseContractItemId;

    @Schema(description = "辅料采购类型")
    private Integer auxiliarySkuType;

    @Schema(description = "采购合同id")
    private Long purchaseContractId;

    @Schema(description = "采购合同编号")
    private String purchaseContractCode;

    @Schema(description = "产品id")
    private Long skuId;

    @Schema(description = "产品编号")
    private String skuCode;

    @Schema(description = "产品名称")
    private String skuName;

    @Schema(description = "客户货号")
    private String cskuCode;

    @Schema(description = "辅料产品id")
    private Long auxiliarySkuId;

    @Schema(description = "辅料产品编号")
    private String auxiliarySkuCode;

    @Schema(description = "辅料产品名称")
    private String auxiliarySkuName;

    @Schema(description = "辅料计量单位")
    private Integer auxiliarySkuUnit;

    @Schema(description = "辅料图片")
    private SimpleFile auxiliarySkuPicture;

    @Schema(description = "产品采购数量")
    private Integer skuQuantity;

    @Schema(description = "辅料采购数量")
    private Integer auxiliaryQuantity;

    @Schema(description = "规格描述")
    private String auxiliaryDescription;

    @Schema(description = "备注")
    private String auxiliaryRemark;

}
