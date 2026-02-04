
package com.syj.eplus.module.sms.controller.admin.quotation.vo;

import cn.iocoder.yudao.framework.excel.core.convert.VolumeConvert;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.deepoove.poi.data.PictureRenderData;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ExcelIgnoreUnannotated
@HeadFontStyle(fontName = "宋体",fontHeightInPoints = 11)
public class QuotationItemExportVO {

//    @ExcelProperty(value = "供应商编号")
//    private String venderCode;

    @ExcelProperty(value = "Ventak SKU No")
    private String skuCode;

    @ExcelProperty(value = "Customer SKU No")
    private String cskuCode;

    @ExcelProperty(value = "Buyer Image")
    private String bztp;

    //主图
    private SimpleFile mainPicture;

    @ExcelProperty(value = "Vertak Product Image")
    private WriteCellData<Void> cptp;

    //主图打印中间参数
    private PictureRenderData mainPictureRender;

    //产品英文名称
    @ExcelProperty(value = "Product Name")
    private String nameEng;

    //产品英文描述
    @ExcelProperty(value = "Vertak Product Description")
    private String descriptionEng;

    //报价
    private JsonAmount quotation;
    @ExcelProperty(value = "FOB Cost(USD)")
    private String quotationAmount;

    //包装方式
    @ExcelProperty(value = "Package")
    private String packageTypeName;

    //采购总价
    @ExcelProperty(value = "采购总价")
    private String totalPurchase;

    //=
//    @ExcelProperty(value = "pol")
//    private String pol;

    //数量
    @ExcelProperty(value = "MOQ(pcs)")
    private String moq;

    @ExcelProperty(value = "PK Size(QTY/ Inner Box)")
    //内箱装量
    private Integer qtyPerInnerbox;

    @ExcelProperty(value = "PK Size(QTY/Outer carton)")
    //外箱装量
    private Integer qtyPerOuterbox;

    //外箱净重
    @ExcelProperty(value = "N.W.(KGS)")
    private String outerboxNetweight;

    //外箱毛重
    @ExcelProperty(value = "G.W.(KGS)")
    private String outerboxGrossweight;

    @ExcelProperty(value = "Carton Length (CM)")
    private String outerboxLength;

    @ExcelProperty(value = "Carton Width (CM)")
    private String outerboxWidth;

    @ExcelProperty(value = "Carton Height (CM)")
    private String outerboxHeight;

    @ExcelProperty(value = "Carton CBM", converter = VolumeConvert.class)
    private String outerboxVolume;

    @ExcelProperty(value = "20ft Loading(pcs)")
    private String twentyFootCabinetNum;

    @ExcelProperty(value = "40ft Loading(pcs)")
    private String fortyFootCabinetNum;

    @ExcelProperty(value = "40ft HC Loading(pcs)")
    private String fortyFootContainerNum;

    @ExcelProperty(value = "HS code")
    private String hsCode;

    private LocalDateTime quoteDate;
    @ExcelProperty(value = "Lead Time (Days)")
    private Long quoteDays;
}