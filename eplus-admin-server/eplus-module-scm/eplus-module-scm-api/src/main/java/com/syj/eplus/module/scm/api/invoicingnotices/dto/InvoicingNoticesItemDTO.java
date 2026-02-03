package com.syj.eplus.module.scm.api.invoicingnotices.dto;

import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class InvoicingNoticesItemDTO {
    /**
     * 采购合同号
     */
    private String purchaseContractCode;
    /**
     * 采购序号
     */
    private Integer purchaseSortNum;
    /**
     * 产品编号
     */
    private String skuCode;
    /**
     * 客户货号
     */
    private String cskuCode;
    /**
     * 报关数量
     */
    private Integer declarationQuantity;
    /**
     * HS编码
     */
    private String hsCode;
    /**
     * 通知开票数量
     */
    private BigDecimal invoicNoticesQuantity;
    /**
     * 海关计量单位
     */
    private String hsMeasureUnit;
    /**
     * 开票品名
     */
    private String invoicSkuName;
    /**
     * 开票单价
     */
    private BigDecimal invoicPrice;
    /**
     * 发票登记状态
     */
    private Integer inveicRegisteredStatus;
    /**
     * 已登记数量
     */
    private Integer inveicRegisteredQuantity;
    /**
     * 采购数量
     */
    private Integer purchaseTotalQuantity;
    /**
     * 采购含税单价
     */
    private JsonAmount purchaseWithTaxPrice;
    /**
     * 采购币种
     */
    private String purchaseCurrency;
    /**
     * 客户名称
     */
    private String custName;
    /**
     * 客户编号
     */
    private String custCode;
    /**
     * 销售合同编号
     */
    private String saleContractCode;
    /**
     * 手工开票通知
     */
    private Integer manuallyFlag;
    /**
     * 销售明细主键
     */
    private Long saleContractItemId;

    /**
     * 唯一编号
     */
    private String uniqueCode;


    /**
     * 来源编号
     */
    private String sourceUniqueCode;

    /**
     * 出运发票号
     */
    private String shipInvoiceCode;

    /**
     * 来源明细主键
     */
    private Long purchaseContractItemId;

    /**
     * 出运明细明细主键
     */
    private Long shipmentItemId;

    /**
     * 税率
     */
    private BigDecimal taxRate;

    /**
     * 规格
     */
    private List<JsonSpecificationEntity> specificationList;

    /**
     * 是否分箱
     */
    private Integer splitBoxFlag;

    /**
     * 外箱装量
     */
    private Integer qtyPerOuterbox;
}
