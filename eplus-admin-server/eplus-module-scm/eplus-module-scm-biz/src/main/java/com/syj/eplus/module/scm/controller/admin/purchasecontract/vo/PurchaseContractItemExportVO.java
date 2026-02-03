package com.syj.eplus.module.scm.controller.admin.purchasecontract.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.baomidou.mybatisplus.annotation.TableField;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ExcelIgnoreUnannotated
@HeadFontStyle(fontName = "宋体",fontHeightInPoints = 11)
public class PurchaseContractItemExportVO {
    /**
     * 主键
     */
    @ExcelProperty(value = "序号")
    private Long id;

    /**
     * 客户货号
     */
    @ExcelProperty(value = "客户货号")
    private String cskuCode;

    /**
     * 产品编号
     */
    @ExcelProperty(value = "产品编号")
    private String skuCode;

    @ExcelProperty(value = "中文品名")
    private String skuName;

    @ExcelProperty(value = "描述")
    private String description;

    @ExcelProperty(value = "数量")
    private Integer quantity;

    @ExcelProperty(value = "单位")
    private String skuUnit;
    /**
     * 交货日期
     */
    private LocalDateTime planArriveDate;

    @ExcelProperty(value = "交货日期")
    private String planArriveDateFormat;

    /**
     * 含税单价
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount withTaxPrice;

    @ExcelProperty(value = "含税单价")
    private BigDecimal withTaxPriceAmount;

    /**
     * 税率
     */
    private BigDecimal taxRate;


    @ExcelProperty(value = "总金额")
    private BigDecimal purchaseTotalAmount;

    /**
     * 销售合同编码
     */
    @ExcelProperty(value = "销售合同编码")
    private String saleContractCode;

    /**
     * 客户编号
     */
    @ExcelProperty(value = "客户编号")
    private String custCode;

    /**
     * 客户编号
     */
    @ExcelProperty(value = "客户名称")
    private String custName;

    /**
     * 采购类型
     */
    @CompareField(value = "采购类型",enumType = "purchase_type")
    private Integer purchaseType;

    /**
     * 客户编号
     */
    @ExcelProperty(value = "采购类型")
    private String purchaseTypeName;

    /**
     * 来源编号
     */
    @ExcelProperty(value = "来源编号")
    private String sourceUniqueCode;

    /**
     * 箱数
     */
    @ExcelProperty(value = "箱数")
    private Integer boxCount;

    /**
     * 内箱装量
     */
    @ExcelProperty(value = "内箱装量")
    private Integer qtyPerInnerbox;

    /**
     * 外箱装量
     */
    @ExcelProperty(value = "外箱装量")
    private Integer qtyPerOuterbox;

    /**
     * 外箱装量
     */
    @ExcelProperty(value = "来源计划编号")
    private String purchasePlanCode;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;

    @Schema(description = "海关编码")
    private String hsCode;

}