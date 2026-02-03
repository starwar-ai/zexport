package com.syj.eplus.module.scm.dal.dataobject.invoicingnoticesItem;


import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonSpecificationEntityListHandler;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@TableName(value = "scm_invoicing_notices_item", autoResultMap = true)
@KeySequence("scm_invoicing_notices_item_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "开票通知明细")
public class InvoicingNoticesItem extends BaseDO {

    @TableId
    @Schema(description = "主键")
    private Long id;
    @Schema(description = "开票通知单号")
    private String invoicNoticesCode;

    @Schema(description = "采购合同号")
    private String purchaseContractCode;

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

    @Schema(description = "已登票数量")
    private Integer inveicRegisteredQuantity;

    @Schema(description = "采购数量")
    private Integer purchaseTotalQuantity;

    @Schema(description = "采购含税单价")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
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

    @Schema(description = "唯一编号")
    private String uniqueCode;

    @Schema(description = "来源来源号")
    private String sourceUniqueCode;

    /**
     * 出运发票号
     */
    private String shipInvoiceCode;

    @Schema(description = "采购合同明细主键")
    private Long purchaseContractItemId;


    @Schema(description = "出运明细明细主键")
    private Long shipmentItemId;

    @Schema(description = "跟单员")
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept manager;

    /**
     * 退税率
     */
    private BigDecimal taxRate;


    /**
     * 总金额
     */
    @TableField(exist = false)
    private BigDecimal totalAmount;

    /**
     *转换前数量
     */
    private Integer baseInvoiceQuantity;

    /**
     * 已开票数量
     */
    @TableField(exist = false)
    private Integer invoicedQuantity;

    /**
     * 产品品名
     */
    private String skuName;

    /**
     * 规格
     */
    @TableField(typeHandler = JsonSpecificationEntityListHandler.class)
    private List<JsonSpecificationEntity> specificationList;

    /**
     * 是否分箱
     */
    private Integer splitBoxFlag;

    /**
     * 外箱装量
     */
    private Integer qtyPerOuterbox;


    /**
     * 聚合ids
     */
    @TableField(exist = false)
    private List<Long> ids;
}
