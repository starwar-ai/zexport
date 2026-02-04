package com.syj.eplus.module.scm.api.paymentapply.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonWeightTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.SimpleFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ApplyerPurchaseContractItemDTO {
    @TableId
    private Long id;

    private Integer ver;

    private Integer sortNum;

    private Long skuId;

    private String skuCode;

    private Long custId;

    private String custCode;

    private String cskuCode;

    private Integer quantity;

    private Long venderId;

    private String venderCode;

    private LocalDateTime planArriveDate;

    private Integer checkStatus;

    private Integer checkedQuantity;

    private Integer receiveStatus;

    private Integer receivedQuantity;

    private Integer exchangeQuantity;

    private Integer returnQuantity;

    private Long purchaseContractId;

    private String purchaseContractCode;

    private Integer qtyPerInnerbox;

    private Integer qtyPerOuterbox;

    private BigDecimal packageLength;

    private Integer syncQuoteFlag;

    private BigDecimal packageWidth;

    private BigDecimal packageHeight;

    private Integer packageUnit;

    @TableField(typeHandler = JsonWeightTypeHandler.class)
    private JsonWeight singleGrossweight;

    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount packagingPrice;

    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount shippingPrice;

    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount unitPrice;

    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount totalPrice;

    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount withTaxPrice;

    private BigDecimal taxRate;

    private Integer purchaseType;

    private Long purchaseUserId;

    private String purchaseUserName;

    private Long purchaseUserDeptId;

    private String purchaseUserDeptName;

    private String venderProdCode;

    private LocalDateTime quoteDate;

    private Integer freightFlag;

    private Integer packageFlag;

    private List<Long> packageType;

    private String currency;

    private Integer faxFlag;

    private Integer moq;

    private Integer boxCount;

    private Integer warehousingType;

    private String wmsIds;

    private String wmsNames;

    private Integer freeFlag;

    private Integer delivery;

    private String purchaseUrl;

    private String remark;

    private Integer auxiliarySkuFlag;

    private Integer auxiliarySkuType;

    private String specRemark;

    private List<SimpleFile> annex;

    private String auxiliarySaleContractCode;

    private String auxiliaryPurchaseContractCode;

    private Long auxiliarySkuId;

    private String auxiliarySkuCode;

    private String auxiliaryCskuCode;

    private JsonAmount checkCost;

    private String skuUnit;

    private Integer ownBrandFlag;

    private Integer custProFlag;

    private JsonAmount packageCost;

    private Long saleContractItemId;

    private Integer invoiceStatus;

    private Integer invoicedQuantity;

    private String invoicedCurrency;

    private BigDecimal invoicedAmount;

    private BigDecimal paymentAmount;

    private Integer changeFlag;

    private BigDecimal appliedAmount;

    private BigDecimal applyAmount;

    @Schema(description = "规格")
    /**
     * 规格
     */
    private List<JsonSpecificationEntity> specificationList;

    /**
     * 是否分箱
     */
    private Integer splitBoxFlag;
}