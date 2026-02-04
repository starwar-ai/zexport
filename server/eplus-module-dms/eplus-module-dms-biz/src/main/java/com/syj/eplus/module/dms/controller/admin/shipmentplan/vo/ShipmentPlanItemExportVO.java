package com.syj.eplus.module.dms.controller.admin.shipmentplan.vo;


import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.syj.eplus.framework.common.entity.SimpleFile;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ExcelIgnoreUnannotated
@HeadFontStyle(fontName = "宋体",fontHeightInPoints = 11)
public class ShipmentPlanItemExportVO {
    @ExcelProperty(value = "中文品名")
    private String name;
    @ExcelProperty(value = "品名")
    private String nameEng;

    @ExcelProperty(value = "数量")
    private Integer shippingQuantity;

    @ExcelProperty(value = "箱数")
    private Integer boxCount;

    @ExcelProperty(value = "箱数+外箱单位")
    private String boxCountLabel;

    @ExcelProperty(value = "外箱毛重")
    private BigDecimal outerboxGrossweight;

    @ExcelProperty(value = "总毛重")
    private BigDecimal totalGrossweight;

    @ExcelProperty(value = "单箱毛重")
    private BigDecimal singleGrossweight;

    @ExcelProperty(value = "外箱净重")
    private BigDecimal outerboxNetweight;

    @ExcelProperty(value = "总净重")
    private BigDecimal totalNetweight;

    @ExcelProperty(value = "单箱净重")
    private BigDecimal singleNetweight;

    @ExcelProperty(value = "总体积")
    private BigDecimal totalVolume;

    @ExcelProperty(value = "收货人")
    private String receivePerson;

    @ExcelProperty(value = "通知人")
    private String notifyPerson;

    @ExcelProperty(value = "唛头")
    private String frontShippingMark;

    @ExcelProperty(value = "包装尺寸")
    private String spec;

    @ExcelProperty(value = "外箱单位")
    private Integer unitPerOuterbox;

    @ExcelProperty(value = "发票号")
    private String invoiceCode;

    @ExcelProperty(value = "客户PO")
    private String custPo;

    @ExcelProperty(value = "客户货号")
    private String cskuCode;

    @ExcelProperty(value = "报关英文品名")
    private String customsDeclarationNameEng;

    @ExcelProperty(value = "HS编码")
    private String hsCode;

    @ExcelProperty(value = "报关中文品名")
    private String declarationName;

    private SimpleFile mainPicture;

    @ExcelProperty(value = "Vertak Product Image")
    private WriteCellData<Void> cptp;

    @ExcelProperty(value = "采购合同编号")
    private String purchaseContractCode;

    @ExcelProperty(value = "供应商名称")
    private String venderName;
    @ExcelProperty(value = "外箱体积")
    private BigDecimal outerboxVolume;

    @ExcelProperty(value = "外箱规格长度")
    private BigDecimal outerboxLength;

    @ExcelProperty(value = "外箱规格宽度")
    private BigDecimal outerboxWidth;

    @ExcelProperty(value = "外箱规格高度")
    private BigDecimal outerboxHeight;

    @ExcelProperty(value = "跟单员")
    private String manager;

    /**
     * 条形码
     *
     * @author 波波
     */
    @ExcelProperty(value = "条形码")
    private String barcode;
}
