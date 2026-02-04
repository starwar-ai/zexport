package com.syj.eplus.module.dms.controller.admin.declaration.vo;

import cn.iocoder.yudao.framework.excel.core.convert.VolumeConvert;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ExcelIgnoreUnannotated
@HeadFontStyle(fontName = "宋体",fontHeightInPoints = 11)
public class DeclarationItemExportVO {
    /**
     * 主键
     */
    @ExcelProperty(value = "序号")
    private Integer sortNum;

    /**
     * 海关编码
     */
    @Schema(description = "海关编码")
    @ExcelProperty(value = "海关编码")
    private String hsCode;

    /**
     * 报关要素
     */
    @Schema(description = "报关要素")
    @ExcelProperty(value = "报关要素")
    private String customsDeclareElements;

    /**
     * 中文名称
     */
    @Schema(description = "报关中文品名")
    @ExcelProperty(value = "报关中文品名")
    private String declarationName;

    /**
     * 英文名称
     */
    @Schema(description = "报关英文品名")
    @ExcelProperty(value = "报关英文品名")
    private String customsDeclarationNameEng;


    /**
     * 出运数量
     */
    @Schema(description = "出运数量")
    @ExcelProperty(value = "出运数量")
    private Integer shippingQuantity;

    /**
     * 报关单位
     */
    @Schema(description = "报关单位")
    @ExcelProperty(value = "报关单位")
    private String declarationUnit;

    /**
     * 海关计量单位
     */
    @Schema(description = "海关计量单位")
    @ExcelProperty(value = "海关计量单位")
    private String hsMeasureUnit;


    /**
     * 报关数量
     */
    @Schema(description = "报关数量")
    @ExcelProperty(value = "报关数量")
    private Integer declarationQuantity;

    /**
     * 箱数
     */
    @Schema(description = "箱数")
    @ExcelProperty(value = "箱数")
    private Integer boxCount;

    /**
     * 外箱单位
     */
    @Schema(description = "外箱单位")
    @ExcelProperty(value = "外箱单位")
    private String unitPerOuterbox;

    /**
     * 计价方式
     */
    @Schema(description = "计价方式")
    @ExcelProperty(value = "计价方式")
    private Integer pricingMethod;

    /**
     * 报关单价
     */
    @Schema(description = "报关单价")
    @ExcelProperty(value = "报关单价")
    private Double declarationUnitPriceAmount;

    /**
     * 报关总价
     */
    @Schema(description = "报关总价")
    @ExcelProperty(value = "报关总价")
    private Double declarationTotalPrice;

    /**
     * 外箱体积
     */
    @Schema(description = "外箱体积")
   @ExcelProperty(value = "外箱体积", converter = VolumeConvert.class)
    private BigDecimal outerboxVolume;

    /**
     * 总体积
     */
    @Schema(description = "总体积")
    @ExcelProperty(value = "总体积")
    private BigDecimal totalVolume;

    /**
     * 外箱毛重
     */
    @Schema(description = "外箱毛重")
    @ExcelProperty(value = "外箱毛重")
    private BigDecimal outerboxGrossweightweight;


    @Schema(description = "总毛重")
    @ExcelProperty(value = "外箱毛重")
    private BigDecimal totalGrossweightweight;

    /**
     * 外箱净重
     */
    @Schema(description = "外箱净重")
    @ExcelProperty(value = "外箱净重")
    private BigDecimal outerboxNetweightweight;

    /**
     * 总净重
     */
    @Schema(description = "总净重")
    @ExcelProperty(value = "总净重")
    private BigDecimal totalNetweightweight;


    /**
     * 退税率
     */
    @Schema(description = "退税率")
    @ExcelProperty(value = "退税率%")
    private Double taxRefundRate;

    /**
     * 客户货号
     */
    @Schema(description = "客户货号")
    @ExcelProperty(value = "客户货号")
    private String cskuCode;

    /**
     * 条形码
     *
     * @author 波波
     */
    @Schema(description = "条形码")
    @ExcelProperty(value = "条形码")
    private String barcode;

}