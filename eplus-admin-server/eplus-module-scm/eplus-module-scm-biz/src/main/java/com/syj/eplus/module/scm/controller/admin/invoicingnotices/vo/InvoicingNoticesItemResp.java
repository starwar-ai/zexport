package com.syj.eplus.module.scm.controller.admin.invoicingnotices.vo;

import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/09/26/14:47
 * @Description:
 */
@Data
public class InvoicingNoticesItemResp {
    @Schema(description = "主键")
    private Long id;
    @Schema(description = "开票通知单号")
    private String invoicNoticesCode;


    @Schema(description = "出运发票号")
    private String shipInvoiceCode;

    @Schema(description = "采购合同号")
    private String purchaseContractCode;

    @Schema(description = "采购合同明细id")
    private String purchaseContractItemId;

    @Schema(description = "采购序号")
    private Integer purchaseSortNum;

    @Schema(description = "产品编号")
    private String skuCode;

    @Schema(description = "基础产品编号")
    private String basicSkuCode;

    @Schema(description = "客户货号")
    private String cskuCode;

    @Schema(description = "报关数量")
    private Integer declarationQuantity;

    @Schema(description = "HS编码")
    private String hsCode;

    @Schema(description = "通知开票数量")
    private BigDecimal invoicNoticesQuantity;

    @Schema(description = "海关计量单位")
    private String hsMeasureUnit;

    @Schema(description = "开票品名")
    private String invoicSkuName;

    @Schema(description = "开票单价")
    private BigDecimal invoicPrice;

    @Schema(description = "发票登记状态")
    private Integer inveicRegisteredStatus;

    @Schema(description = "发票登记数量")
    private Integer inveicRegisteredQuantity;

    @Schema(description = "采购数量")
    private Integer purchaseTotalQuantity;

    @Schema(description = "采购含税单价")
    private JsonAmount purchaseWithTaxPrice;

    @Schema(description = "采购币种")
    private String purchaseCurrency;

    @Schema(description = "客户名称")
    private String custName;

    @Schema(description = "客户编号")
    private String custCode;

    @Schema(description = "销售合同编号")
    private String saleContractCode;

    @Schema(description = "手工开票通知")
    private Integer manuallyFlag;

    @Schema(description = "销售明细主键")
    private Long saleContractItemId;

    @Schema(description = "付款主体主键")
    private Long companyId;

    @Schema(description = "付款主体")
    private String companyName;

    @Schema(description = "供应商名称")
    private String venderName;

    @Schema(description = "供应商编号")
    private String venderCode;

    @Schema(description = "转换前数量")
    private Integer baseInvoiceQuantity;

    @Schema(description = "税率")
    private BigDecimal taxRate;

    @Schema(description = "规格")
    private List<JsonSpecificationEntity> specificationList;

    @Schema(description = "是否分箱")
    private Integer splitBoxFlag;

     @Schema(description = "外箱装量")
    private Integer qtyPerOuterbox;
}
