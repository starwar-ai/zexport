package com.syj.eplus.module.controller.admin.sendproduct.vo;

import com.syj.eplus.framework.common.entity.SimpleFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 寄件产品新增/修改 Request VO")
@Data
public class SendProductSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "22556")
    private Long id;

    @Schema(description = "寄件id")
    private Long sendId;

    @Schema(description = "序号")
    private Integer sortNum;

    @Schema(description = "物件来源")
    private Integer goodsSource;

    @Schema(description = "产品编号")
    private String skuCode;

    @Schema(description = "产品名称", example = "张三")
    private String skuName;

    @Schema(description = "图片", requiredMode = Schema.RequiredMode.REQUIRED)
    private SimpleFile picture;

    @Schema(description = "数量")
    private Integer quantity;


    //前端传参
    @Schema(description = "图片", requiredMode = Schema.RequiredMode.REQUIRED)
    private SimpleFile mainPicture;

}