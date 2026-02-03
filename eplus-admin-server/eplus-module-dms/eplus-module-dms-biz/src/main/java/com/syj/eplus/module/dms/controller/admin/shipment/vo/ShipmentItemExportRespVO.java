
package com.syj.eplus.module.dms.controller.admin.shipment.vo;


import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ExcelIgnoreUnannotated
@Accessors(chain = false)
public class ShipmentItemExportRespVO {
    @ExcelProperty("发票号")
    private String invoiceCode;

    @ExcelProperty("序号")
    private Integer sortNum;

    @ExcelProperty("客户名称")
    private String custName;

    @ExcelProperty("客户合同")
    private String custPO;

    @ExcelProperty("外销合同")
    private String saleContractCode;

    @ExcelProperty("订单序号")
    private Integer orderNum;

    @ExcelProperty("销售事业部")
    private String salesDeptName;

    @ExcelProperty("外销员")
    private String salesName;

    @ExcelProperty("采购事业部")
    private String purchaseUserDeptName;

    @ExcelProperty("采购员")
    private String purchaseUserName;

    @Schema(description = "合同日期")
    private LocalDateTime salesCreateDate;

    @Schema(description = "合同交期")
    private LocalDateTime custDeliveryDate;

    @ExcelProperty("采购合同")
    private String purchaseContractCode;

    @ExcelProperty("厂商简称")
    private String venderName;

    @ExcelProperty("客户货号")
    private String cskuCode;

    @ExcelProperty("基础产品编号")
    private String basicSkuCode;

    @ExcelProperty("中文品名")
    private String name;

    @ExcelProperty("报关中文品名")
    private String declarationName;

    @ExcelProperty("出货数量")
    private Integer shippingQuantity;

    @ExcelProperty("报关数量")
    private Integer declarationQuantity;

    @ExcelProperty("海关计量单位")
    private String hsMeasureUnit;

    @ExcelProperty("总净重")
    private BigDecimal totalNetweightDecimal;

    @ExcelProperty("出运日期")
    private LocalDateTime shipDate;

    @ExcelProperty("外销单价")
    private BigDecimal saleUnitPriceBigDecimal;
    
    @ExcelProperty("外销单价币种")
    private String saleUnitPriceCurrency;

    @ExcelProperty("报关价")
    private BigDecimal declarationUnitPriceDecimal;
    
    @ExcelProperty("报关价币种")
    private String declarationUnitPriceCurrency;

    @ExcelProperty("报关金额")
    private BigDecimal declarationAmountDecimal;
    
    @ExcelProperty("报关金额币种")
    private String declarationAmountCurrency;

    @ExcelProperty("采购单价")
    private BigDecimal purchaseWithTaxPriceDecimal;
    
    @ExcelProperty("采购单价币种")
    private String purchaseWithTaxPriceCurrency;

    @ExcelProperty("采购总价")
    private BigDecimal purchaseTotalDecimal;
    
    @ExcelProperty("采购总价币种")
    private String purchaseTotalCurrency;

    @ExcelProperty("退税率%")
    private BigDecimal taxRefundRate;

    @ExcelProperty("退税金额")
    private BigDecimal taxRefundPriceBigDecimal;
    
    @ExcelProperty("退税金额币种")
    private String taxRefundPriceCurrency;

    @ExcelProperty("HS编码")
    private String hsCode;

    @ExcelProperty("出运单号")
    private String code;

    @ExcelProperty("是否出仓")
    private String outboundFlagString;

    @ExcelProperty("合同日期")
    private LocalDateTime contractDate;

    @ExcelProperty("合同交期")
    private LocalDateTime contractDeliveryDate;

    @ExcelProperty("出运日期")
    private LocalDateTime actualShipDate;

    @ExcelProperty("客户归属")
    private String customerType;

    @ExcelProperty("客户分类")
    private String customerSource;
}
