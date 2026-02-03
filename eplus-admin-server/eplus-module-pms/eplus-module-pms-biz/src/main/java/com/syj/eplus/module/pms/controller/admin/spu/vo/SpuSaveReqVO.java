package com.syj.eplus.module.pms.controller.admin.spu.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 商品新增/修改 Request VO")
@Data
public class SpuSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "1112")
    private Long id;

    @Schema(description = "名称", example = "王五")
    private String name;

    @Schema(description = "编号")
    private String code;

    @Schema(description = "产品条码")
    private String barcode;

    @Schema(description = "英文名称")
    private String nameEng;

    @Schema(description = "品牌编号", example = "20012")
    private Long brandId;

    @Schema(description = "商品分类", example = "28690")
    private Long categoryId;

    @Schema(description = "产品类型", example = "2")
    private String spuType;

    @Schema(description = "计量单位", example = "1")
    private String unitType;

    @Schema(description = "商品状态")
    private Integer onshelfFlag;

    @Schema(description = "审核状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "审核状态不能为空")
    private Integer auditStatus;

    @Schema(description = "商品说明", example = "随便")
    private String description;

    @Schema(description = "商品说明英文")
    private String descriptionEng;

    @Schema(description = "海关编码")
    private Long hsCodeId;

    @Schema(description = "是否自主品牌", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否自主品牌不能为空")
    private Integer ownBrandFlag;

    @Schema(description = "产品规格")
    @NotNull(message = "产品规格不能为空")
    private String spec;

    @Schema(description = "海关编码版本")
    @NotNull(message = "海关编码版本不能为空")
    private Integer hsCode_var;

}