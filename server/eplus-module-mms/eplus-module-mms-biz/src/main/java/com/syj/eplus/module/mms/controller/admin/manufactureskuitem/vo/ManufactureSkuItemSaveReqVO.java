package com.syj.eplus.module.mms.controller.admin.manufactureskuitem.vo;


import com.syj.eplus.framework.common.entity.JsonStockDTO;
import com.syj.eplus.framework.common.entity.SimpleFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 加工单子产品新增/修改 Request VO")
@Data
public class ManufactureSkuItemSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "10158")
    private Long id;

    @Schema(description = "加工单产品id", example = "17983")
    private Long manufactureSkuId;

    @Schema(description = "加工单id", example = "2510")
    private Long manufactureId;

    @Schema(description = "产品id", example = "2482")
    private Long skuId;

    @Schema(description = "产品编号")
    private String skuCode;

    @Schema(description = "客户产品编号")
    private String cskuCode;

    @Schema(description = "产品名称", example = "芋艿")
    private String skuName;

    @Schema(description = "产品数量")

    private Integer quantity;

    @Schema(description = "配比")
    private Integer ratio;

    @Schema(description = "产品图片")
    private SimpleFile mainPicture;

    /**
     * 库存列表
     */
    private List<JsonStockDTO> stockList;

}