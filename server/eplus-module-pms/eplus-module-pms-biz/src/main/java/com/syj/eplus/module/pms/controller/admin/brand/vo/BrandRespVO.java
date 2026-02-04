package com.syj.eplus.module.pms.controller.admin.brand.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.SimpleFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 品牌 Response VO")
@Data
@ExcelIgnoreUnannotated
public class BrandRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "编号")
    @ExcelProperty("编号")
    private String code;

    @Schema(description = "名称")
    @ExcelProperty("名称")
    private String name;

    @Schema(description = "流水号长度")
    @ExcelProperty("流水号长度")
    private String nameEng;

    @Schema(description = "商品说明")
    @ExcelProperty("商品说明")
    private String description;

    @Schema(description = "商品说明英文")
    @ExcelProperty("商品说明英文")
    private String descriptionEng;

    @Schema(description = "是否自主品牌")
    @ExcelProperty("是否自主品牌")
    private Integer ownBrandFlag;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "品牌类型")
    @ExcelProperty("品牌类型")
    private Integer brandType;

    @Schema(description = "客户id")
    @ExcelProperty("客户id")
    private Long custId;

    @Schema(description = "客户编号")
    @ExcelProperty("客户编号")
    private String custCode;

    @Schema(description = "客户名称")
    @ExcelProperty("客户名称")
    private String custName;

    @Schema(description = "品牌logo")
    @ExcelProperty("品牌logo")
    private SimpleFile logo;

}