package com.syj.eplus.module.pms.controller.admin.spu.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 商品 Response VO")
@Data
@ExcelIgnoreUnannotated
public class SpuRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "1112")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "名称", example = "王五")
    @ExcelProperty("名称")
    private String name;

    @Schema(description = "编号")
    @ExcelProperty("编号")
    private String code;

    @Schema(description = "产品条码")
    @ExcelProperty("产品条码")
    private String barcode;

    @Schema(description = "英文名称")
    @ExcelProperty("英文名称")
    private String nameEng;

    @Schema(description = "品牌编号", example = "20012")
    @ExcelProperty("品牌编号")
    private Long brandId;

    @Schema(description = "商品分类", example = "28690")
    @ExcelProperty("商品分类")
    private Long categoryId;

    @Schema(description = "产品类型", example = "2")
    @ExcelProperty("产品类型")
    private String spuType;

    @Schema(description = "计量单位", example = "1")
    @ExcelProperty("计量单位")
    private String unitType;

    @Schema(description = "商品状态")
    @ExcelProperty("商品状态")
    private Integer onshelfFlag;

    @Schema(description = "审核状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("审核状态")
    private Integer auditStatus;

    @Schema(description = "商品说明", example = "随便")
    @ExcelProperty("商品说明")
    private String description;

    @Schema(description = "商品说明英文")
    @ExcelProperty("商品说明英文")
    private String descriptionEng;

    @Schema(description = "海关编码")
    @ExcelProperty("海关编码")
    private Long hsCodeId;

    @Schema(description = "是否自主品牌", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("是否自主品牌")
    private Integer ownBrandFlag;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "产品规格")
    @ExcelProperty("产品规格")
    private String spec;

    @Schema(description = "海关编码版本")
    @ExcelProperty("海关编码版本")
    private Integer hsCode_var;

}