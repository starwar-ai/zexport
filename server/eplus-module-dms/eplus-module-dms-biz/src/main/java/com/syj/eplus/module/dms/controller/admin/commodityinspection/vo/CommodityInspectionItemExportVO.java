package com.syj.eplus.module.dms.controller.admin.commodityinspection.vo;

import cn.iocoder.yudao.framework.excel.core.convert.VolumeConvert;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonWeight;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ExcelIgnoreUnannotated
@HeadFontStyle(fontName = "宋体",fontHeightInPoints = 11)
public class CommodityInspectionItemExportVO {
    /**
     * 主键
     */
    @ExcelProperty(value = "序号")
    private Integer sortNum;

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
     * 申报要素
     */
    //=
    @ExcelProperty(value = "申报要素")
    private String customsDeclareElements;


    /**
     * 数量
     */
    @Schema(description = "数量")
    @ExcelProperty(value = "数量")
    private Integer declarationQuantity;

    private JsonAmount saleUnitPrice;

    @Schema(description = "销售单价")
    @ExcelProperty(value = "销售单价")
    private BigDecimal saleUnitPriceAmount;

    private JsonAmount saleAmount;
    @Schema(description = "金额")
    @ExcelProperty(value = "金额")
    private BigDecimal saleAmountAmount;

    /**
     * 海关计量单位
     */
    @Schema(description = "海关计量单位")
    private String hsMeasureUnit;
    private String hsMeasureUnitName;

    @Schema(description = "采购币种")
    @ExcelProperty(value = "采购币种")
    private String purchaseCurrency;

    private JsonAmount purchaseUnitPrice;
    @Schema(description = "采购单价")
    @ExcelProperty(value = "采购单价")
    private BigDecimal purchaseUnitPriceAmount;
    @Schema(description = "采购总价")
    @ExcelProperty(value = "采购总价")
    private BigDecimal purchaseTotalAmountAmount;

    @Schema(description = "HS编码")
    @ExcelProperty(value = "HS编码")
    private String hsCode;

    @Schema(description = "退税率")
    @ExcelProperty(value = "退税率%")
    private BigDecimal taxRefundRate;

    private JsonAmount taxRefundPrice;
    @Schema(description = "退税金额")
    @ExcelProperty(value = "退税金额")
    private BigDecimal taxRefundPriceAmount;

    //==
    @Schema(description = "采购员编号")
    @ExcelProperty(value = "采购员编号")
    private String purchaseUserCode;

    @Schema(description = "采购员姓名")
    @ExcelProperty(value = "采购员姓名")
    private String purchaseUserName;

    @Schema(description = "供应商名称")
    @ExcelProperty(value = "供应商名称")
    private String venderName;

    @Schema(description = "箱数")
    @ExcelProperty(value = "箱数")
    private Integer boxCount;

    @Schema(description = "外箱单位")
    @ExcelProperty(value = "外箱单位")
    private String unitPerOuterbox;

    @Schema(description = "外箱体积")
    @ExcelProperty(value = "外箱体积", converter = VolumeConvert.class)
    private BigDecimal outerboxVolume;

    @Schema(description = "总体积")
    @ExcelProperty(value = "总体积")
    private BigDecimal totalVolume;

    @Schema(description = "外箱毛重")
    private JsonWeight outerboxGrossweight;

    @Schema(description = "外箱毛重")
    @ExcelProperty(value = "外箱毛重")
    private BigDecimal outerboxGrossweightWeight;

    @Schema(description = "总毛重", requiredMode = Schema.RequiredMode.REQUIRED)
    private JsonWeight totalGrossweight;

    @Schema(description = "总毛重")
    @ExcelProperty(value = "总毛重")
    private BigDecimal totalGrossweightWeight;

    @Schema(description = "外箱净重")
    private JsonWeight outerboxNetweight;

    @Schema(description = "外箱净重")
    @ExcelProperty(value = "外箱净重")
    private BigDecimal outerboxNetweightWeight;

    @Schema(description = "总净重")
    private JsonWeight totalNetweight;

    @Schema(description = "总净重")
    @ExcelProperty(value = "总净重")
    private BigDecimal totalNetweightWeight;

    /**
     * 条形码
     *
     * @author 波波
     */
    @Schema(description = "条形码")
    @ExcelProperty(value = "条形码")
    private String barcode;



}