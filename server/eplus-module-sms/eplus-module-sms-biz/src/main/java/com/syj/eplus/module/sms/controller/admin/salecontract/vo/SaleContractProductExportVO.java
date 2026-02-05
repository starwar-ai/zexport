package com.syj.eplus.module.sms.controller.admin.salecontract.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.metadata.data.WriteCellData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 销售合同产品维度导出 VO")
@Data
@ExcelIgnoreUnannotated
@HeadFontStyle(fontName = "宋体", fontHeightInPoints = 11)
public class SaleContractProductExportVO {

    @ExcelProperty("合同号")
    @Schema(description = "合同号")
    private String saleContractCode;

    @ExcelProperty("报关主体")
    @Schema(description = "报关主体")
    private String foreignTradeCompanyName;

    @ExcelProperty("交易币种")
    @Schema(description = "交易币种")
    private String currency;

    @ExcelProperty("客户PO号")
    @Schema(description = "客户PO号")
    private String custPO;

    @ExcelProperty("接单日期")
    @Schema(description = "接单日期")
    private LocalDateTime inputDate;

    @ExcelProperty("客户交期")
    @Schema(description = "客户交期")
    private LocalDateTime custDeliveryDate;

    @ExcelProperty("客户名称")
    @Schema(description = "客户名称")
    private String custName;

    @ExcelProperty("国家")
    @Schema(description = "国家")
    private String custCountryName;

    @ExcelProperty("贸易国别")
    @Schema(description = "贸易国别")
    private String tradeCountryName;

    @ExcelProperty("客户货号")
    @Schema(description = "客户货号")
    private String cskuCode;

    @ExcelProperty("基础产品编号")
    @Schema(description = "基础产品编号")
    private String basicSkuCode;

    @ExcelProperty("中文品名")
    @Schema(description = "中文品名")
    private String name;

    @ExcelProperty("产品编号")
    @Schema(description = "产品编号")
    private String skuCode;

    @ExcelProperty("数量")
    @Schema(description = "数量")
    private Integer quantity;

    @ExcelProperty("外销单价")
    @Schema(description = "外销单价")
    private BigDecimal unitPriceAmount;

    @ExcelProperty("价格条款")
    @Schema(description = "价格条款")
    private String settlementName;

    @ExcelProperty("英文品名")
    @Schema(description = "英文品名")
    private String nameEng;

    @ExcelProperty("工厂名称")
    @Schema(description = "工厂名称")
    private String venderName;

    @ExcelProperty("采购员")
    @Schema(description = "采购员")
    private String purchaseUserNickname;

    @ExcelProperty("采购员部门")
    @Schema(description = "采购员部门")
    private String purchaseUserDeptName;

    @ExcelProperty("收款方式")
    @Schema(description = "收款方式")
    private String paymentMethod;

    @ExcelProperty("业务员")
    @Schema(description = "业务员")
    private String salesNickname;

    @ExcelProperty("部门名称")
    @Schema(description = "部门名称")
    private String salesDeptName;

    @ExcelProperty("市别")
    @Schema(description = "市别")
    private String marketType;

    @ExcelProperty("汇率")
    @Schema(description = "汇率")
    private BigDecimal exchangeRate;

    @ExcelProperty("总额")
    @Schema(description = "总额")
    private BigDecimal totalAmount;

    @ExcelProperty("销售总金额(USD)")
    @Schema(description = "销售总金额(USD)")
    private BigDecimal totalAmountUsd;

    @ExcelProperty("采购数量")
    @Schema(description = "采购数量")
    private Integer purchaseQuantity;

    @ExcelProperty("采购含税单价")
    @Schema(description = "采购含税单价")
    private BigDecimal withTaxPriceRemoveFreeAmount;

    @ExcelProperty("采购总金额")
    @Schema(description = "采购总金额")
    private BigDecimal purchaseTotalAmount;

    @ExcelProperty("真实锁定数量")
    @Schema(description = "真实锁定数量")
    private Integer realLockQuantity;

    @ExcelProperty("锁定库存单价")
    @Schema(description = "锁定库存单价")
    private BigDecimal stockLockPriceAmount;

    @ExcelProperty("锁定库存总金额")
    @Schema(description = "锁定库存总金额")
    private BigDecimal stockLockTotalPriceAmount;

    @ExcelProperty("入库地点")
    @Schema(description = "入库地点")
    private Integer billStatus;

    @ExcelProperty("待转出运数量")
    @Schema(description = "待转出运数量")
    private Integer pendingTransferShippedQuantity;

    @ExcelProperty("待出运数量")
    @Schema(description = "待出运数量")
    private Integer pendingShippedQuantity;

    @ExcelProperty("单据状态")
    @Schema(description = "单据状态")
    private String statusName;

    @ExcelProperty("预估总费用")
    @Schema(description = "预估总费用")
    private BigDecimal estimatedTotalCost;

    @ExcelProperty("预估毛利")
    @Schema(description = "预估毛利")
    private BigDecimal estimatedGrossProfit;

    @ExcelProperty("出货状态")
    @Schema(description = "出货状态")
    private String shipmentStatus;

    @ExcelProperty("预估毛利率")
    @Schema(description = "预估毛利率")
    private BigDecimal grossProfitMargin;

    @ExcelProperty("回签时间")
    @Schema(description = "回签时间")
    private LocalDateTime signBackDate;

    @ExcelProperty("回签状态")
    @Schema(description = "回签状态")
    private String signBackStatus;

    @ExcelProperty("录入日期")
    @Schema(description = "录入日期")
    private LocalDateTime createTime;

    @ExcelProperty("录入人")
    @Schema(description = "录入人")
    private String creatorName;

    @ExcelProperty("录入人部门")
    @Schema(description = "录入人部门")
    private String creatorDeptName;

    @ExcelProperty("备注")
    @Schema(description = "备注")
    private String remark;

    @ExcelProperty("新客户")
    @Schema(description = "新客户")
    private String newCustomer;

    @ExcelProperty("产品图片")
    @Schema(description = "产品图片")
    private WriteCellData<Void> productImage;

}
