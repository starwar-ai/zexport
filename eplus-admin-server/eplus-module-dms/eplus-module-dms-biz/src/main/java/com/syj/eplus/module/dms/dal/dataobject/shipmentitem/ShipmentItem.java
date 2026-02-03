package com.syj.eplus.module.dms.dal.dataobject.shipmentitem;

import cn.iocoder.yudao.framework.common.annotations.ChangeIgnore;
import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.mybatis.core.exinterface.ChangeExInterface;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.config.handler.*;
import com.syj.eplus.framework.common.entity.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 出运单明细 DO
 *
 * @author du
 */

@TableName(value = "dms_shipment_item", autoResultMap = true)
@KeySequence("dms_shipment_item_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "出运明细明细")
@ExcelIgnoreUnannotated
@Accessors(chain = false)
public class ShipmentItem extends BaseDO implements ChangeExInterface {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 出运单主键
     */
    @CompareField(value = "出运单主键")
    private Long shipmentId;
    /**
     * 发货地点
     */
    @Schema(description = "发货地点")
    @CompareField(value = "发货地点")
    private Integer shippedAddress;

    /**
     * 预计拉柜时间
     */
    @Schema(description = "预计拉柜时间")
    @CompareField(value = "预计拉柜时间")
    @ExcelProperty("预计拉柜时间")
    private LocalDateTime estPickupTime;

    /**
     * 产品主键
     */
    @Schema(description = "产品主键")
    @CompareField(value = "产品主键")
    private Long skuId;

    /**
     * 产品编号
     */
    @Schema(description = "产品编号")
    @CompareField(value = "产品编号")
    @ExcelProperty("产品编号")
    private String skuCode;

    /**
     * 客户货号
     */
    @Schema(description = "客户货号")
    @CompareField(value = "客户货号")
    @ExcelProperty("客户货号")
    private String cskuCode;


    /**
     * 基础产品编号
     */
    @Schema(description = "基础产品编号")
    @CompareField(value = "基础产品编号")
    @ExcelProperty("基础产品编号")
    private String basicSkuCode;

    /**
     * 中文名称
     */
    @Schema(description = "中文名称")
    @CompareField(value = "中文名称")
    @ExcelProperty("中文名称")
    private String name;

    /**
     * 英文名称
     */
    @Schema(description = "英文名称")
    @CompareField(value = "英文名称")
    @ExcelProperty("英文名称")
    private String nameEng;

    /**
     * 报关数量
     */
    @Schema(description = "报关数量")
    @CompareField(value = "报关数量")
    @ExcelProperty("报关数量")
    private Integer declarationQuantity;

    /**
     * 已报关数量
     */
    @Schema(description = "已报关数量")
    @CompareField(value = "已报关数量")
    private Integer declarationQuantityOld;

    /**
     * 本次报关数量
     */
    @Schema(description = "本次报关数量")
    @CompareField(value = "本次报关数量")
    private Integer declarationQuantityCurrent;

    /**
     * 采购数量
     */
    @Schema(description = "采购数量")
    @CompareField(value = "采购数量")
    @ExcelProperty("采购数量")
    private Integer purchaseTotalQuantity;

    /**
     * 销售单价
     */
    @Schema(description = "销售单价", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "销售单价")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount saleUnitPrice;

    /**
     * 销售金额
     */
    @Schema(description = "销售金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "销售金额")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount saleAmount;

    /**
     * 报关单价
     */
    @Schema(description = "报关单价", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "报关单价")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount declarationUnitPrice;

    /**
     * 报关金额
     */
    @Schema(description = "报关金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "报关金额")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount declarationAmount;

    /**
     * 保险费
     */
    @Schema(description = "保险费", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "保险费")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount insuranceFee;

    /**
     * 交货日期
     */
    @Schema(description = "交货日期")
    @CompareField(value = "交货日期")
    @ExcelProperty("交货日期")
    private LocalDateTime deliveryDate;

    /**
     * 是否商检
     */
    @Schema(description = "是否商检")
    @CompareField(value = "是否商检")
    private Integer commodityInspectionFlag;

    /**
     * 商检类型
     */
    @Schema(description = "商检类型")
    @CompareField(value = "商检类型")
    private Integer commodityInspectionType;

    /**
     * HS编码
     */
    @Schema(description = "HS编码")
    @CompareField(value = "HS编码")
    @ExcelProperty("HS编码")
    private String hsCode;

    /**
     * 报关中文品名
     */
    @Schema(description = "报关中文品名")
    @CompareField(value = "报关中文品名")
    @ExcelProperty("报关中文品名")
    private String declarationName;

    /**
     * 报关英文品名
     */
    @Schema(description = "报关英文品名")
    @CompareField(value = "报关英文品名")
    @ExcelProperty("报关英文品名")
    private String customsDeclarationNameEng;

    /**
     * 海关计量单位
     */
    @Schema(description = "海关计量单位")
    @CompareField(value = "海关计量单位")
    private String hsMeasureUnit;

    /**
     * 退税率
     */
    @Schema(description = "退税率")
    @CompareField(value = "退税率")
    private BigDecimal taxRefundRate;

    /**
     * 退税金额
     */
    @Schema(description = "退税金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "退税金额")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount taxRefundPrice;

    /**
     * 采购合同编号
     */
    @Schema(description = "采购合同编号")
    @CompareField(value = "采购合同编号")
    @ExcelProperty("采购合同编号")
    private String purchaseContractCode;

    /**
     * 采购员id
     */
    @Schema(description = "采购员id")
    @CompareField(value = "采购员id")
    private Long purchaseUserId;

    /**
     * 采购员姓名
     */
    @Schema(description = "采购员姓名")
    @CompareField(value = "采购员姓名")
    @ExcelProperty("采购员姓名")
    private String purchaseUserName;

    /**
     * 采购员部门id
     */
    @Schema(description = "采购员部门id")
    @CompareField(value = "采购员部门id")
    private Long purchaseUserDeptId;

    /**
     * 采购员部门名称
     */
    @Schema(description = "采购员部门名称")
    @CompareField(value = "采购员部门名称")
    @ExcelProperty("采购员部门名称")
    private String purchaseUserDeptName;

    /**
     * 采购币种
     */
    @Schema(description = "采购币种")
    @CompareField(value = "采购币种")
    @ExcelProperty("采购币种")
    private String purchaseCurrency;

    /**
     * 采购单价
     */
    @Schema(description = "采购单价", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "采购单价")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount purchaseUnitPrice;

    /**
     * 包装方式
     */
    @Schema(description = "包装方式")
    @CompareField(value = "包装方式")
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
    @CompareField(value = "外箱装量")
    private Integer qtyPerOuterbox;

    /**
     * 内盒装量
     */
    @Schema(description = "内盒装量")
    @CompareField(value = "内盒装量")
    private Integer qtyPerInnerbox;

    /**
     * 箱数
     */
    @Schema(description = "箱数")
    @CompareField(value = "箱数")
    private Integer boxCount;

    /**
     * 总体积
     */
    @Schema(description = "总体积")
    @CompareField(value = "总体积")
    private BigDecimal totalVolume;

    /**
     * 总净重
     */
    @Schema(description = "总净重", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "总净重")
    @TableField(typeHandler = JsonWeightTypeHandler.class)
    private JsonWeight totalNetweight;


    /**
     * 总毛重
     */
    @Schema(description = "总毛重", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "总毛重")
    @TableField(typeHandler = JsonWeightTypeHandler.class)
    private JsonWeight totalGrossweight;

    /**
     * 中文说明
     */
    @Schema(description = "中文说明")
    @CompareField(value = "中文说明")
    private String description;
    /**
     * 英文说明
     */
    @Schema(description = "英文说明")
    @CompareField(value = "英文说明")
    private String descriptionEng;

    /**
     * 仓库编号
     */
    @Schema(description = "仓库编号")
    @CompareField(value = "仓库编号")
    @ExcelProperty("仓库编号")
    private String stockCode;

    /**
     * 仓库名称
     */
    @Schema(description = "仓库名称")
    @CompareField(value = "仓库名称")
    @ExcelProperty("仓库名称")
    private String stockName;

    /**
     * 备注
     */
    @CompareField(value = "备注")
    private String remark;

    /**
     * 佣金金额
     */
    @Schema(description = "佣金金额")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    @CompareField(value = "佣金金额")
    private JsonAmount commissionAmount;

    /**
     * 毛利率
     */
    @Schema(description = "毛利率")
    @CompareField(value = "毛利率")
    private BigDecimal orderGrossProfitRate;

    /**
     * 是否出仓
     */
    @Schema(description = "是否出仓")
    @CompareField(value = "是否出仓")
    private Integer outboundFlag;

    /**
     * 出仓日期
     */
    @Schema(description = "出仓日期")
    @CompareField(value = "出仓日期")
    private LocalDateTime outboundDate;

    /**
     * 报关单位
     */
    @Schema(description = "报关单位")
    @CompareField(value = "报关单位")
    @ExcelProperty("报关单位")
    private String declarationUnit;

    /**
     * 已报关数
     */
    @Schema(description = "已报关数")
    @CompareField(value = "已报关数")
    @ExcelProperty("已报关数")
    private Integer declaredQuantity;

    /**
     * 是否转结汇单
     */
    @Schema(description = "是否转结汇单")
    @CompareField(value = "是否转结汇单")
    private Integer settleOrderFlag;

    /**
     * 价格条款
     */
    @Schema(description = "价格条款")
    @CompareField(value = "价格条款")
    @ExcelProperty("价格条款")
    private String settlementTermType;

    @Schema(description = "客户主键")
    @CompareField(value = "客户主键")
    private Long custId;

    @Schema(description = "客户编号")
    @CompareField(value = "客户编号")
    @ExcelProperty("客户编号")
    private String custCode;

    @Schema(description = "客户名称")
    @CompareField(value = "客户名称")
    @ExcelProperty("客户名称")
    private String custName;

    @Schema(description = "外销合同号", requiredMode = Schema.RequiredMode.REQUIRED)
    @CompareField(value = "外销合同号")
    @ExcelProperty("外销合同号")
    private String saleContractCode;

    /**
     * 是否转报关单
     */
    @Schema(description = "是否转报关单")
    @CompareField(value = "是否转报关单")
    private Integer isToDeclaration;

    /**
     * 转报关单人
     */
    @Schema(description = "转报关单人")
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    @CompareField(value = "转报关单人")
    private UserDept declarationUser;

    /**
     * 转报关单时间
     */
    @Schema(description = "转报关单时间")
    @CompareField(value = "转报关单时间")
    private LocalDateTime declarationDate;

    /**
     * 是否转结汇单
     */
    @Schema(description = "是否转结汇单")
    @CompareField(value = "是否转结汇单")
    private Integer isToSettlementForm;

    /**
     * 转报结汇单人
     */
    @Schema(description = "转结汇单人")
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    @CompareField(value = "转结汇单人")
    private UserDept settlementFormUser;

    /**
     * 转结汇单时间
     */
    @Schema(description = "转结汇单时间")
    @CompareField(value = "转结汇单时间")
    private LocalDateTime settlementFormDate;

    /**
     * 是否转商检单
     */
    @Schema(description = "是否转商检单")
    @CompareField(value = "是否转商检单")
    private Integer isToCommodityInspection;

    /**
     * 转商检单人
     */
    @Schema(description = "转商检单人")
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    @CompareField(value = "转商检单人")
    private UserDept commodityInspectionUser;

    /**
     * 转结汇单时间
     */
    @Schema(description = "转商检单时间")
    @CompareField(value = "转商检单时间")
    private LocalDateTime commodityInspectionDate;

    @Schema(description = "跟单员")
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    @CompareField(value = "跟单员")
    private UserDept manager;

    /**
     * 出运数量
     */
    @Schema(description = "出运数量")
    @CompareField(value = "出运数量")
    @ExcelProperty("出运数量")
    private Integer shippingQuantity;

    /**
     * 供应商名称
     */
    @Schema(description = "供应商名称")
    @CompareField(value = "供应商名称")
    @ExcelProperty("供应商名称")
    private String venderName;

    /**
     * 供应商id
     */
    @Schema(description = "供应商id")
    @CompareField(value = "供应商id")
    private Long venderId;

    /**
     * 供应商code
     */
    @Schema(description = "供应商code")
    @CompareField(value = "供应商code")
    private String venderCode;

    /**
     * 供应商名称
     */
    @Schema(description = "应付供应商名称")
    private String payVenderName;

    /**
     * 供应商id
     */
    @Schema(description = "应付供应商id")
    private Long payVenderId;

    /**
     * 供应商code
     */
    @Schema(description = "应付供应商code")
    private String payVenderCode;
    /**
     * 交易币别
     */
    @Schema(description = "交易币别")
    @CompareField(value = "交易币别")
    private String currency;

    /**
     * 结汇方式
     */
    @Schema(description = "结汇方式")
    @CompareField(value = "结汇方式")
    private Long settlementId;

    /**
     * 结汇名称
     */
    @Schema(description = "结汇名称")
    @CompareField(value = "结汇名称")
    private String settlementName;

    /**
     * 状态
     */
    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "销售明细主键")
    private Long saleContractItemId;

    @TableField(exist = false)
    private Integer changeFlag;

    @TableField(exist = false)
    private String sourceCode;

    @TableField(exist = false)
    private List<JsonEffectRange> effectRangeList;


    /**
     * 船代费用均摊金额
     */
    @Schema(description = "船代费用均摊金额")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount forwarderShareAmount;

    /**
     * 唯一编号
     */
    private String uniqueCode;


    /**
     * 来源编号
     */
    private String sourceUniqueCode;

    /**
     * 结汇数量
     */
    private Integer settlementQuantity;

    /**
     * 开票数量
     */
    private BigDecimal invoicedQuantity;

    /**
     * 本次采购数量
     */
    private BigDecimal thisPurchaseQuantity;

    /**
     * 转开票状态
     */
    private Integer invoiceStatus;

    /**
     * 转拉柜通知单标识 0-否 1-是
     */
    private Integer converNoticeFlag;


    @Schema(description = "含税单价")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount purchaseWithTaxPrice;

    /**
     * 来源销售合同编号
     */
    private String sourceSaleContractCode;


    /**
     * 外销员
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept sales;



    /**
     * 拆分采购标记
     */
    private Integer purchaseModel;

    /**
     * 出库数量
     */
    private Integer outQuantity;

    /**
     * 出库日期
     */
    private LocalDateTime outDate;

    /**
     * 主体id列表
     */
    @TableField(exist = false)
    private List<Long> companyIdList;

    /**
     * 真实锁定数量
     */
    private Integer realLockQuantity;

    @TableField(exist = false)
    private Integer skuDeletedFlag;

    /**
     * 入库状态
     */
    private Integer billStatus;

    /**
     * 单位
     */
    private Integer measureUnit;

    /**
     * 客户PO号
     */
    private String custPo;

    /**
     * 库存成本
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount stockCost;

    /**
     * 验货状态
     */
    private Integer checkStatus;


    @TableField(exist = false)
    private JsonCompanyPath companyPath;

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
     * 外箱单位
     */
    @Schema(description = "外箱单位")
    @CompareField(value = "外箱单位")
    private Integer unitPerOuterbox;

    /**
     * 图片
     */
    @ChangeIgnore
    @TableField(typeHandler = JsonFileTypeHandler.class)
    private SimpleFile mainPicture;

    /**
     * 缩略图
     */
    @ChangeIgnore
    private String thumbnail;

    /**
     * 首节点id
     */
    @TableField(exist = false)
    private Long firstNodeId;

    /**
     * 来源明细id
     */
    private Long sourceItemId;

    /**
     * 拆分数量
     */
    private Integer splitQuantity;

    /**
     * 是否包含赠品
     */
    private Integer freeFlag;

    /**
     * 是否需要加工
     */
    private Integer processFlag;

    /**
     * 库存采购合同号
     */
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> stockPurchaseContractCodes;

    /**
     * 合同详情列表（用于弹框显示）
     */
    @TableField(exist = false)
    private List<SimpleContractDetailDTO> contractDetails;

    /**
     * 条形码
     *
     * @author 波波
     */
    @Schema(description = "条形码")
    private String barcode;
}
