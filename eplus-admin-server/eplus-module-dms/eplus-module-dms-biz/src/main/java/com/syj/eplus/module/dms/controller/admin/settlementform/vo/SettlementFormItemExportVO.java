package com.syj.eplus.module.dms.controller.admin.settlementform.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.baomidou.mybatisplus.annotation.TableField;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ExcelIgnoreUnannotated
@HeadFontStyle(fontName = "宋体",fontHeightInPoints = 11)
public class SettlementFormItemExportVO {
    /**
     * 主键
     */
    @ExcelProperty(value = "序号")
    private Integer sortNum;

    /**
     * 订单号
     */
    @ExcelProperty(value = "订单号")
    private String code;

    /**
     * 订单号
     */
    @ExcelProperty(value = "订单序号")
    private String settlementFormId;
    
    @ExcelProperty(value = "产品编号")
    private String skuCode;
    
    @ExcelProperty(value = "报关中文品名")
    private String declarationName;

    @Schema(description = "报关英文品名")
    private String customsDeclarationNameEng;


    /**
     * 出运数量
     */
    @Schema(description = "出运数量")
    @ExcelProperty(value = "出运数量")
    private Integer shippingQuantity;

    /**
     * 单位
     */
    @ExcelProperty(value = "单位")
    private Integer unit;

    /**
     * 销售单价
     */
    private JsonAmount saleUnitPrice;

    /**
     * 销售单价
     */
    @ExcelProperty(value = "销售单价")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private BigDecimal saleUnitPriceAmount;

    @ExcelProperty(value = "销售单价美元")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private BigDecimal saleUnitPriceAmountUSD;


    private JsonAmount saleAmount;

    @ExcelProperty(value = "金额")
    private BigDecimal saleAmountAmount;

    @ExcelProperty(value = "金额USD")
    private BigDecimal saleAmountUSD;

    /**
     * 交货日期
     */
    private LocalDateTime deliveryDate;

    @Schema(description = "交货日期")
    @ExcelProperty(value = "交货日期")
    private String deliveryDateFormat;

    //=
    @ExcelProperty(value = "客户合同")
    private String custPo;


    @Schema(description = "客户货号")
    @ExcelProperty(value = "客户货号")
    private String cskuCode;

    @Schema(description = "海关单位")
    @ExcelProperty(value = "海关单位")
    private String hsMeasureUnit;

    private String hsMeasureUnitName;

    @Schema(description = "采购币种")
    @ExcelProperty(value = "采购币种")
    private String purchaseCurrency;

    @Schema(description = "采购单价")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount purchaseUnitPrice;

    @Schema(description = "采购单价")
    @ExcelProperty(value = "采购单价")
    private BigDecimal purchaseUnitPriceAmount;

    @Schema(description = "采购总价")
    @ExcelProperty(value = "采购总价")
    private BigDecimal purchaseTotalAmountAmount;

    //=
    @ExcelProperty(value = "产品条码")
    private String sku;

    @Schema(description = "采购合同")
    @ExcelProperty(value = "采购合同")
    private String purchaseContractCode;
    /**
     * 采购员id
     */
    @ExcelProperty(value = "采购员id")
    private Long purchaseUserId;

    //=
    @ExcelProperty(value = "采购员编号")
    private String purchaseUserCode;

    /**
     * 采购员姓名
     */
    @Schema(description = "采购员名称")
    @ExcelProperty(value = "采购员名称")
    private String purchaseUserName;


    @Schema(description = "厂商编号")
    @ExcelProperty(value = "厂商编号")
    private String venderCode;

    @Schema(description = "厂商名称")
    @ExcelProperty(value = "厂商名称")
    private String venderName;

    //=
    @ExcelProperty(value = "工厂货号")
    private String venderskuCode;

    @Schema(description = "包装方式")
    @ExcelProperty(value = "包装方式")
    private List<Long> packageType;


    @Schema(description = "包装方式中文名", example = "1")
    @ExcelProperty("包装方式中文名")
    private String packageTypeName;

    @Schema(description = "包装方式英文名", example = "1")
    @ExcelProperty("包装方式英文名")
    private String packageTypeEngName;

    //=
    @ExcelProperty(value = "特殊要求")
    private Integer specific;

    @Schema(description = "内盒装量")
    @ExcelProperty(value = "内盒装量")
    private Integer qtyPerInnerbox;


    @Schema(description = "外箱装量")
    @ExcelProperty(value = "外箱装量")
    private Integer qtyPerOuterbox;

    @Schema(description = "箱数")
    @ExcelProperty(value = "箱数")
    private Integer boxCount;

    @Schema(description = "外箱单位")
    @ExcelProperty(value = "外箱单位")
    private String unitPerOuterbox;

    @Schema(description = "外箱长度")
    @ExcelProperty(value = "外箱长度")
    private BigDecimal outerboxLength;

    @Schema(description = "外箱宽度")
    @ExcelProperty(value = "外箱宽度")
    private BigDecimal outerboxWidth;

    @Schema(description = "外箱高度")
    @ExcelProperty(value = "外箱高度")
    private BigDecimal outerboxHeight;

    @Schema(description = "外箱体积")
    @ExcelProperty(value = "外箱体积")
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
     * 中文说明
     */
    @Schema(description = "中文说明")
    @ExcelProperty(value = "中文说明")
    private String description;
    /**
     * 英文说明
     */
    @Schema(description = "英文说明")
    @ExcelProperty(value = "英文说明")
    private String descriptionEng;

    @Schema(description = "HS编码")
    @ExcelProperty(value = "HS编码")
    private String hsCode;

    @Schema(description = "退税率")
    @ExcelProperty(value = "退税率%")
    private BigDecimal taxRefundRate;

    @Schema(description = "退税金额")
    private JsonAmount taxRefundPrice;

    @Schema(description = "退税金额")
    private JsonAmount taxRefundPriceAmount;
    @ExcelProperty(value = "退税金额")
    private BigDecimal taxRefundPriceAmountAmount;

    @Schema(description = "客户编号")
    @ExcelProperty(value = "客户编号")
    private String custCode;

    @Schema(description = "客户名称")
    @ExcelProperty(value = "客户名称")
    private String custName;

    @Schema(description = "业务员")
    private UserDept manager;

    @Schema(description = "业务员编号")
    @ExcelProperty(value = "业务员编号")
    private String managerCode;

    @Schema(description = "业务员名称")
    @ExcelProperty(value = "业务员名称")
    private String managerName;

    //=
    @Schema(description = "收款方式编号")
    @ExcelProperty(value = "收款方式编号")
    private String settlementCode;

    //=
    @Schema(description = "收款方式名称")
    @ExcelProperty(value = "收款方式名称")
    private String settlementName;

    //==
    @Schema(description = "申报要素")
    @ExcelProperty(value = "申报要素")
    private String customsDeclareElements;

    //==
    @Schema(description = "备注")
    @ExcelProperty(value = "备注")
    private String remark;

    @Schema(description = "基础产品编号")
    @ExcelProperty(value = "基础产品编号")
    private String basicSkuCode;

    @Schema(description = "英文名称")
    @ExcelProperty(value = "英文名称")
    private String nameEng;
    @Schema(description = "名称")
    @ExcelProperty(value = "名称")
    private String name;

    /**
     * 条形码
     *
     * @author 波波
     */
    @Schema(description = "条形码")
    @ExcelProperty(value = "条形码")
    private String barcode;
}