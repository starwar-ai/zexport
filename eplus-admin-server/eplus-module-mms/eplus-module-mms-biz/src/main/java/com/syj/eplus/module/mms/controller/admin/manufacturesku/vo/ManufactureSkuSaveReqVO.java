package com.syj.eplus.module.mms.controller.admin.manufacturesku.vo;


import com.syj.eplus.framework.common.entity.SimpleFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 加工单产品新增/修改 Request VO")
@Data
public class ManufactureSkuSaveReqVO {


    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "12773")
    private Long id;

    @Schema(description = "加工单id", example = "9979")
    private Long manufactureId;

    @Schema(description = "产品id", example = "6010")
    private Long skuId;

    @Schema(description = "产品编号")
    private String skuCode;

    @Schema(description = "客户产品编号")
    private String cskuCode;

    @Schema(description = "产品名称", example = "芋艿")
    private String skuName;

    @Schema(description = "产品数量")
    private Integer quantity;

    @Schema(description = "产品图片")
    private SimpleFile mainPicture;

    @Schema(description = "销售合同id", example = "7925")
    private Long smsContractId;

    @Schema(description = "销售合同编号")
    private String smsContractCode;

}