package com.syj.eplus.module.dms.dal.dataobject.declarationitem;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.config.handler.*;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 报关单明细 DO
 *
 * @author du
 */

@TableName(value = "dms_declaration_item", autoResultMap = true)
@KeySequence("dms_declaration_item_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeclarationItem extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;

    @Schema(description = "外销合同号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String saleContractCode;

    /**
     * 出运明细明细id
     */
    @Schema(description = "出运明细明细id")
    private Long shipmentItemId;

    /**
     * 海关编码
     */
    @Schema(description = "HS编码")
    private String hsCode;
    /**
     * 报关单Id
     */
    @Schema(description = "报关单Id")
    private Long declarationId;

    /**
     * 报关要素
     */
    @Schema(description = "报关要素")
    private String declarationElement;
    /**
     * 中文名称
     */
    @Schema(description = "报关中文名称")
    private String declarationName;
    /**
     * 英文名称
     */
    @Schema(description = "报关英文名称")
    private String customsDeclarationNameEng;
    /**
     * 客户货号
     */
    @Schema(description = "客户货号")
    private String cskuCode;

    /**
     * 海关计量单位
     */
    @Schema(description = "海关计量单位")
    private String hsMeasureUnit;

    /**
     * 报关数量
     */
    @Schema(description = "报关数量")
    private Integer declarationQuantity;
    /**
     * 箱数
     */
    @Schema(description = "箱数")
    private Integer boxCount;
    /**
     * 外箱单位
     */
    @Schema(description = "外箱单位")
    private Integer unitPerOuterbox;
    /**
     * 计价方式
     */
    @Schema(description = "计价方式")
    private Integer pricingMethod;
    /**
     * 报关单据
     */
    @Schema(description = "报关单据")
    private String declarationInvoices;

    /**
     * 总体积
     */
    @Schema(description = "总体积")
    private BigDecimal totalVolume;

    @Schema(description = "总毛重", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField(typeHandler = JsonWeightTypeHandler.class)
    private JsonWeight totalGrossweight;

    @Schema(description = "退税率")
    private BigDecimal taxRefundRate;

    @Schema(description = "客户主键")
    private Long custId;

    @Schema(description = "客户编号")
    private String custCode;

    @Schema(description = "客户名称")
    private String custName;

    /**
     * 外销币种
     */
    @Schema(description = "外销币种")
    private String currency;

    @Schema(description = "收款方式主键")
    private Long settlementId;

    @Schema(description = "收款方式名称")
    private String settlementName;

    /**
     * 价格条款
     */
    @Schema(description = "价格条款")
    private String settlementTermType;

    /**
     * 发货地点
     */
    @Schema(description = "发货地点")
    private Integer shippedAddress;
    /**
     * 预计拉柜时间
     */
    @Schema(description = "预计拉柜时间")
    private LocalDateTime estPickupTime;
    /**
     * 产品主键
     */
    @Schema(description = "产品主键")
    private Long skuId;
    /**
     * 产品编号
     */
    @Schema(description = "产品编号")
    private String skuCode;

    /**
     * 中文名称
     */
    @Schema(description = "中文名称")
    private String name;
    /**
     * 英文名称
     */
    @Schema(description = "英文名称")
    private String nameEng;

    /**
     * 总采购数量
     */
    @Schema(description = "总采购数量")
    private Integer purchaseTotalQuantity;
    /**
     * 销售单价
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    @Schema(description = "销售单价" , requiredMode = Schema.RequiredMode.REQUIRED)
    private JsonAmount saleUnitPrice;
    /**
     * 销售金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    @Schema(description = "销售金额" , requiredMode = Schema.RequiredMode.REQUIRED)
    private JsonAmount saleAmount;
    /**
     * 报关单价
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    @Schema(description = "报关单价" , requiredMode = Schema.RequiredMode.REQUIRED)
    private JsonAmount declarationUnitPrice;
    /**
     * 报关金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    @Schema(description = "报关金额", requiredMode = Schema.RequiredMode.REQUIRED)
    private JsonAmount declarationAmount;

    /**
     * 保险费
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    @Schema(description = "保险费", requiredMode = Schema.RequiredMode.REQUIRED)
    private JsonAmount insuranceFee;
    /**
     * 交货日期
     */
    @Schema(description = "交货日期")
    private LocalDateTime deliveryDate;

    /**
     * 是否商检
     */
    @Schema(description = "是否商检")
    private Integer commodityInspectionFlag;

    /**
     * 商检类型
     */
    @Schema(description = "商检类型")
    private Integer commodityInspectionType;
    /**
     * 退税金额
     */
    @Schema(description = "退税金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount taxRefundPrice;
    /**
     * 采购合同编号
     */
    @Schema(description = "采购合同编号")
    private String purchaseContractCode;
    /**
     * 采购员id
     */
    @Schema(description = "采购员id")
    private Long purchaseUserId;
    /**
     * 采购员姓名
     */
    @Schema(description = "采购员姓名")
    private String purchaseUserName;
    /**
     * 采购员部门id
     */
    @Schema(description = "采购员部门id")
    private Long purchaseUserDeptId;
    /**
     * 采购员部门名称
     */
    @Schema(description = "采购员部门名称")
    private String purchaseUserDeptName;
    /**
     * 采购币种
     */
    @Schema(description = "采购币种")
    private String purchaseCurrency;
    /**
     * 采购单价
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    @Schema(description = "采购单价", requiredMode = Schema.RequiredMode.REQUIRED)
    private JsonAmount purchaseWithTaxPrice;
    /**
     * 包装方式
     */
    @Schema(description = "包装方式")
    @TableField(typeHandler = LongListTypeHandler.class)
    private List<Long> packageType;

    @Schema(description = "包装方式中文名", example = "1")
    @ExcelProperty("包装方式中文名")
    @TableField(exist = false)
    private String packageTypeName;

    @Schema(description = "包装方式英文名", example = "1")
    @ExcelProperty("包装方式英文名")
    @TableField(exist = false)
    private String packageTypeEngName;
    /**
     * 外箱装量
     */
    @Schema(description = "外箱装量")
    private Integer qtyPerOuterbox;
    /**
     * 内盒装量
     */
    @Schema(description = "内盒装量")
    private Integer qtyPerInnerbox;

    /**
     * 总净重
     */
    @Schema(description = "总净重", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField(typeHandler = JsonWeightTypeHandler.class)
    private JsonWeight totalNetweight;

    /**
     * 中文说明
     */
    @Schema(description = "中文说明")
    private String description;
    /**
     * 英文说明
     */
    @Schema(description = "英文说明")
    private String descriptionEng;
    /**
     * 仓库编号
     */
    @Schema(description = "仓库编号")
    private String stockCode;
    /**
     * 仓库名称
     */
    @Schema(description = "仓库名称")
    private String stockName;
    /**
     * 备注
     */
    private String remark;
    /**
     * 佣金金额
     */
    @Schema(description = "佣金金额")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount commissionAmount;
    /**
     * 毛利率
     */
    @Schema(description = "毛利率")
    private BigDecimal orderGrossProfitRate;
    /**
     * 是否出仓
     */
    @Schema(description = "是否出仓")
    private Integer outboundFlag;
    /**
     * 出仓日期
     */
    @Schema(description = "出仓日期")
    private LocalDateTime outboundDate;
    /**
     * 报关单位
     */
    @Schema(description = "报关单位")
    private String declarationUnit;
    /**
     * 已报关数
     */
    @Schema(description = "已报关数")
    private Integer declaredQuantity;
    /**
     * 是否转结汇单
     */
    @Schema(description = "是否转结汇单")
    private Integer settleOrderFlag;


    /**
     * 是否转报关单
     */
    @Schema(description = "是否转报关单")
    private Integer isToDeclaration;

    /**
     * 转报关单人
     */
    @Schema(description = "转报关单人")
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept declarationUser;

    /**
     * 转报关单时间
     */
    @Schema(description = "转报关单时间")
    private LocalDateTime declarationDate;

    /**
     * 是否转结汇单
     */
    @Schema(description = "是否转结汇单")
    private Integer isToSettlementForm;

    /**
     * 转报结汇单人
     */
    @Schema(description = "转结汇单人")
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept settlementFormUser;

    /**
     * 转结汇单时间
     */
    @Schema(description = "转结汇单时间")
    private LocalDateTime settlementFormDate;

    /**
     * 是否转商检单
     */
    @Schema(description = "是否转商检单")
    private Integer isToCommodityInspection;

    /**
     * 转商检单人
     */
    @Schema(description = "转商检单人")
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept commodityInspectionUser;

    /**
     * 转结汇单时间
     */
    @Schema(description = "转商检单时间")
    private LocalDateTime commodityInspectionDate;

    @Schema(description = "业务员")
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept manager;

    /**
     * 出运数量
     */
    @Schema(description = "出运数量")
    private Integer shippingQuantity;

    /**
     * 供应商名称
     */
    @Schema(description = "供应商名称")
    private String venderName;

    /**
     * 供应商id
     */
    @Schema(description = "供应商id")
    private String venderId;

    /**
     * 供应商code
     */
    @Schema(description = "供应商code")
    private String venderCode;

    /**
     * 客户PO号
     */
    private String custPo;

    /**
     * 报关要素
     */
    private String customsDeclareElements;

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
     * 是否包含赠品
     */
    private Integer freeFlag;

    /**
     * 条形码
     *
     * @author 波波
     */
    @Schema(description = "条形码")
    private String barcode;
}