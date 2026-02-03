package com.syj.eplus.module.scm.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonFileListTypeHandler;
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

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/07/17:09
 * @Description:
 */
@Data
@Schema(description = "采购明细")
public class ApplyerPurchaseContractItem {
    @Schema(description = "主键")
    @TableId
    private Long id;

    @Schema(description = "版本")
    private Integer ver;

    @Schema(description = "序号")
    private Integer sortNum;

    @Schema(description = "产品skuid")
    private Long skuId;

    @Schema(description = "产品编号")
    private String skuCode;

    @Schema(description = "客户ID")
    private Long custId;

    @Schema(description = "客户编号")
    private String custCode;

    @Schema(description = "客户货号")
    private String cskuCode;

    @Schema(description = "采购数量")
    private Integer quantity;

    @Schema(description = "供应商id")
    private Long venderId;

    @Schema(description = "供应商编号")
    private String venderCode;

    @Schema(description = "交货日期")
    private LocalDateTime planArriveDate;

    @Schema(description = "验货状态")
    private Integer checkStatus;

    @Schema(description = "已验货数量")
    private Integer checkedQuantity;

    @Schema(description = "收货状态")
    private Integer receiveStatus;

    @Schema(description = "已收货数量")
    private Integer receivedQuantity;

    @Schema(description = "退货量")
    private Integer exchangeQuantity;

    @Schema(description = "换货量")
    private Integer returnQuantity;

    @Schema(description = "采购合同单号")
    private Long purchaseContractId;

    @Schema(description = "采购合同编号")
    private String purchaseContractCode;

    @Schema(description = "内箱装量")
    private Integer qtyPerInnerbox;

    @Schema(description = "外箱装量")
    private Integer qtyPerOuterbox;

    @Schema(description = "包装规格长度")
    private BigDecimal packageLength;

    @Schema(description = "是否同步供应商")
    private Integer syncQuoteFlag;

    @Schema(description = "包装规格宽度")
    private BigDecimal packageWidth;

    @Schema(description = "包装规格高度")
    private BigDecimal packageHeight;

    @Schema(description = "包装规格单位")
    private Integer packageUnit;

    @Schema(description = "单品毛重")
    @TableField(typeHandler = JsonWeightTypeHandler.class)
    private JsonWeight singleGrossweight;

    @Schema(description = "包装价")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount packagingPrice;

    @Schema(description = "运费")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount shippingPrice;

    @Schema(description = "采购单价")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount unitPrice;

    @Schema(description = "总价")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount totalPrice;

    @Schema(description = "含税总价")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount withTaxPrice;

    @Schema(description = "税率")
    private BigDecimal taxRate;

    @Schema(description = "采购类型")
    private Integer purchaseType;

    @Schema(description = "采购员id")
    private Long purchaseUserId;

    @Schema(description = "采购员姓名")
    private String purchaseUserName;

    @Schema(description = "采购员部门id")
    private Long purchaseUserDeptId;

    @Schema(description = "采购员部门名称")
    private String purchaseUserDeptName;

    @Schema(description = "工厂货号")
    private String venderProdCode;

    @Schema(description = "报价日期")
    private LocalDateTime quoteDate;

    @Schema(description = "是否含运费")
    private Integer freightFlag;

    @Schema(description = "是否含包装")
    private Integer packageFlag;

    @Schema(description = "包装方式")
    private List<Long> packageType;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "是否含包装")
    private Integer faxFlag;

    @Schema(description = "最小起购量")
    private Integer moq;

    @Schema(description = "箱数")
    private Integer boxCount;

    @Schema(description = "入库状态")
    private Integer warehousingType;

    @Schema(description = "采购合同回写仓库ids")
    private String wmsIds;

    @Schema(description = "采购合同回写仓库名称列表")
    private String wmsNames;

    @Schema(description = "是否赠品")
    private Integer freeFlag;

    @Schema(description = "交期")
    private Integer delivery;

    @Schema(description = "采购链接")
    private String purchaseUrl;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "是否通用辅料")
    private Integer auxiliarySkuFlag;

    @Schema(description = "辅料采购类型")
    private Integer auxiliarySkuType;

    @Schema(description = "规格描述")
    private String specRemark;

    @Schema(description = "附件")
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> annex;

    @Schema(description = "辅料属于的销售合同")
    private String auxiliarySaleContractCode;

    @Schema(description = "辅料属于的采购合同")
    private String auxiliaryPurchaseContractCode;

    @Schema(description = "辅料属于的采购合同产品id")
    private Long auxiliarySkuId;

    @Schema(description = "辅料属于的采购合同产品编号")
    private String auxiliarySkuCode;

    @Schema(description = "辅料属于的采购合同产品客户货号")
    private String auxiliaryCskuCode;

    /**
     * 验货费
     */
    @Schema(description = "验货费")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount checkCost;

    @Schema(description = "计量单位")
    private String skuUnit;

    @Schema(description = "自主品牌标记")
    private Integer ownBrandFlag;

    @Schema(description = "客户产品标记")
    private Integer custProFlag;

    /**
     * 包装费
     */
    @Schema(description = "包装费")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount packageCost;

    @Schema(description = "销售明细主键")
    private Long saleContractItemId;

    @Schema(description = "开票状态")
    private Integer invoiceStatus;

    @Schema(description = "已开票数量")
    private Integer invoicedQuantity;

    @Schema(description = "开票币种")
    private String invoicedCurrency;

    @Schema(description = "已开票金额")
    private BigDecimal invoicedAmount;

    @Schema(description = "已支付金额")
    private BigDecimal paymentAmount;

    @Schema(description = "变更标志", accessMode = Schema.AccessMode.READ_ONLY)
    @TableField(exist = false)
    private Integer changeFlag;

    @Schema(description = "已申请金额")
    private BigDecimal appliedAmount;

    @Schema(description = "本次申请金额")
    private BigDecimal applyAmount;

    @Schema(description = "实际申请金额")
    private BigDecimal realApplyAmount;

    @Schema(description = "销售合同编号")
    private String saleContractCode;

    @Schema(description = "产品名称")
    private String skuName;

    @Schema(description = "处理标志")
    private Integer handleFlag;

    @Schema(description = "验货时间")
    private LocalDateTime inspectionTime;

    @Schema(description = "规格")
    /**
     * 规格
     */
    private List<JsonSpecificationEntity> specificationList;

    /**
     * 是否分箱
     */
    private Integer splitBoxFlag;

    /**
     * 开船日期
     */
    private LocalDateTime estDepartureTime;

    /**
     * 出入库状态
     */
    private Integer billStatus;
}