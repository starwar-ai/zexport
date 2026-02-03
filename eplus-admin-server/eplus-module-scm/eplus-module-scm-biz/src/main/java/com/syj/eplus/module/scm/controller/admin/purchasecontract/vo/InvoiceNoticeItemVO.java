package com.syj.eplus.module.scm.controller.admin.purchasecontract.vo;

import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class InvoiceNoticeItemVO {

    @Schema(description = "采购合同编号")
    private String purchaseContractCode;

    @Schema(description = "采购合同编号")
    private Integer purchaseSortNum;

    @Schema(description = "采购合同明细主键")
    private Long purchaseContractItemId;

    @Schema(description = "采购合同明细主键")
    private String sourceUniqueCode;

    @Schema(description = "产品主键")
    private Long skuId;

    @Schema(description = "产品编号")
    private String skuCode;

    @Schema(description = "基础产品编号")
    private String basicSkuCode;

    @Schema(description = "产品名称")
    private String skuName;

    @Schema(description = "客户货号")
    private String cskuCode;

    @Schema(description = "HS编码")
    private String hsCode;

    @Schema(description = "海关计量单位")
    private String hsMeasureUnit;

    @Schema(description = "开票品名")
    private String skuNoticeName;

    @Schema(description = "开票单价")
    private JsonAmount noticePrice;

    @Schema(description = "本次开票金额")
    private JsonAmount noticeAmount;

//    @Schema(description = "发票登记状态")
//    private Integer noticeStatus;

    @Schema(description = "采购数量")
    private Integer purchaseTotalQuantity;

    @Schema(description = "已开票数量")
    private Integer invoicedQuantity;

    @Schema(description = "开票状态")
    private Integer invoiceStatus;

    @Schema(description = "采购单价")
    private JsonAmount purchasePrice;

    @Schema(description = "采购币别")
    private String purchaseCurrency;

    @Schema(description = "客户id")
    private Long custId;

    @Schema(description = "客户编号")
    private String custCode;

    @Schema(description = "客户名称")
    private String custName;

    @Schema(description = "销售合同编号")
    private String saleContractCode;

     @Schema(description = "销售合同明细id")
   private Long saleContractItemId;

    @Schema(description = "采购含税单价")
    private JsonAmount purchaseWithTaxPrice;

    @Schema(description = "计量单位")
    private String skuUnit;

    @Schema(description = "通知开票数量")
    private BigDecimal invoicNoticesQuantity;

    @Schema(description = "外箱装量")
    private Integer qtyPerOuterbox;

    @Schema(description = "规格")
    private List<JsonSpecificationEntity> specificationList;

    @Schema(description = "是否分箱")
    private Integer splitBoxFlag;

    @Schema(description = "转换前数量")
    private Integer baseInvoiceQuantity;

    @Schema(description = "开票总额")
    private JsonAmount invoiceTotalAmount;
}
