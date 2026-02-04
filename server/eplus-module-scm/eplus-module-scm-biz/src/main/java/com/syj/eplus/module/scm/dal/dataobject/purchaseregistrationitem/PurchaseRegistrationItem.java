package com.syj.eplus.module.scm.dal.dataobject.purchaseregistrationitem;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

/**
 * 采购跟单登记明细 DO
 *
 * @author du
 */

@TableName(value = "scm_purchase_registration_item", autoResultMap = true)
@KeySequence("scm_purchase_registration_item_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "采购跟单明细")
public class PurchaseRegistrationItem extends BaseDO {

    @TableId
    @Schema(description = "主键")
    private Long id;

    @Schema(description = "开票通知明细主键")
    private Long invoicingNoticesItemId;

    @Schema(description = "采购合同明细主键")
    private Long purchaseContractItemId;

    @Schema(description = "跟单登记主键")
    private Long registrationId;

    @Schema(description = "税率")
    private BigDecimal taxRate;

    @Schema(description = "本次登票数")
    private Integer invoicThisQuantity;

    @Schema(description = "出运数量")
    private BigDecimal shippingQuantity;

    @Schema(description = "海关计量单位")
    private String hsMeasureUnit;

    @Schema(description = "报关数量")
    private Integer declarationQuantity;

    @Schema(description = "开票单价")
    private BigDecimal invoicPrice;

    @Schema(description = "报关品名")
    private String declarationName;

    @Schema(description = "税票编号")
    private String invoiceCode;

    @Schema(description = "采购数量")
    private Integer purchaseTotalQuantity;

    @Schema(description = "采购含税单价")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount purchaseWithTaxPrice;

    @Schema(description = "采购币种")
    private String purchaseCurrency;

    @Schema(description = "采购合同号")
    private String purchaseContractCode;

    @Schema(description = "跟单员")
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept manager;

    @Schema(description = "销售合同号")
    private String saleContractCode;

    @Schema(description = "中文品名")
    private String skuName;

    @Schema(description = "产品编号")
    private String skuCode;

    @Schema(description = "基础产品编号")
    private String basicSkuCode;

    @Schema(description = "HS编码")
    private String hsCode;

    @Schema(description = "客户货号")
    private String cskuCode;

    @Schema(description = "采购税率")
    private BigDecimal purchaseTaxRate;

    @Schema(description = "供应商编号")
    private String venderCode;

    @Schema(description = "供应商名称")
    private String venderName;

    @Schema(description = "销售明细主键")
    private Long saleContractItemId;

    @Schema(description = "通知开票数量")
    private Integer invoicNoticesQuantity;

    @Schema(description = "出运发票号")
    private String shipInvoiceCode;

    @Schema(description = "报关品名")
    private String invoicSkuName;

    @Schema(description = "已登票数量")
    private Integer inveicRegisteredQuantity;
}
