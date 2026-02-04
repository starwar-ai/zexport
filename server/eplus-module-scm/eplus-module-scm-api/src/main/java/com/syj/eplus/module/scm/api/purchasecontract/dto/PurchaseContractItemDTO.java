package com.syj.eplus.module.scm.api.purchasecontract.dto;

import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PurchaseContractItemDTO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "序号")
    private Integer sortNum;

    @Schema(description = "采购类型")
    private Integer purchaseModel;

    @Schema(description = "辅料id")
    private Long skuId;

    @Schema(description = "辅料编号")
    private String skuCode;

    @Schema(description = "辅料名称")
    private String skuName;

    @Schema(description = "计量单位")
    private String skuUnit;

    @Schema(description = "规格描述")
    private String specRemark;

    @Schema(description = "计划采购量")
    private Integer planQuantity;

    @Schema(description = "实际采购量")
    private Integer quantity;

    @Schema(description = "供应商Id")
    private Long venderId;

    @Schema(description = "供应商编号")
    private String venderCode;

    @Schema(description = "供应商名称")
    private String venderName;

    @Schema(description = "应付供应商Id")
    private Long payVenderId;

    @Schema(description = "应付供应商编号")
    private String payVenderCode;

    @Schema(description = "应付供应商名称")
    private String payVenderName;

    @Schema(description = "供应商报价")
    private JsonAmount unitPrice;

    @Schema(description = "销售合同")
    private String saleContractCode;

    @Schema(description = "销售合同明细")
    private Long saleContractItemId;

    @Schema(description = "采购合同")
    private String purchaseContractCode;

    @Schema(description = "采购合同主键")
    private Long purchaseContractId;

    @Schema(description = "产品编号")
    private String auxiliarySkuCode;

    @Schema(description = "产品品名")
    private String auxiliarySkuName;

    @Schema(description = "客户货号")
    private String auxiliaryCskuCode;
    /**
     * 采购员编码
     */
    private Long purchaseUserId;
    /**
     * 采购员名称
     */
    @CompareField(value = "采购员名称")
    private String purchaseUserName;
    /**
     * 采购员部门编码
     */
    private Long purchaseUserDeptId;
    /**
     * 采购员部门名称
     */
    @CompareField(value = "采购员部门名称")
    private String purchaseUserDeptName;

    /**
     * 入库状态
     */
    private Integer billStatus;

    /**
     * 采购币种
     */
    private String purchaseCurrency;

    /**
     * 交货日期
     */
    private LocalDateTime deliveryDate;

    @Schema(description = "规格")
    private List<JsonSpecificationEntity> specificationList;

    /**
     * 是否分箱
     */
    private Integer splitBoxFlag;

    /**
     * 箱数
     */
    private Integer boxCount;

    /**
     * 开票状态
     */
    private Integer invoiceStatus;

    /**
     * 已开票数量
     */
    private Integer invoicedQuantity;

    /**
     * 描述
     */
    private String description;

    /**
     * 条形码
     *
     * @author 波波
     */
    private String barcode;
}
