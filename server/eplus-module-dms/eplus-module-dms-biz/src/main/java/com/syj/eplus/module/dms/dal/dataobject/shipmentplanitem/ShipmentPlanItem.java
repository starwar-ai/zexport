package com.syj.eplus.module.dms.dal.dataobject.shipmentplanitem;

import cn.iocoder.yudao.framework.common.annotations.ChangeIgnore;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 出运计划单明细 DO
 *
 * @author du
 */

@TableName(value = "dms_shipment_plan_item", autoResultMap = true)
@KeySequence("dms_shipment_plan_item_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentPlanItem extends BaseDO {

    @Schema(description = "主键")
    @TableId
    private Long id;

    @Schema(description = "出运计划单主键")
    private Long shipmentPlanId;

    @Schema(description = "产品主键")
    private Long skuId;

    @Schema(description = "产品编号")
    private String skuCode;

    @Schema(description = "客户货号")
    private String cskuCode;

    @Schema(description = "基础产品编号")
    private String basicSkuCode;

    @Schema(description = "中文品名")
    private String name;

    @Schema(description = "英文品名")
    private String nameEng;

    @Schema(description = "采购数量")
    private Integer purchaseQuantity;

    @Schema(description = "销售单价")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount saleUnitPrice;

    @Schema(description = "销售金额")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount saleAmount;

    @Schema(description = "是否商检")
    private Integer commodityInspectionFlag;

    @Schema(description = "HS编码")
    private String hsCode;

    @Schema(description = "退税率")
    private BigDecimal taxRefundRate;

    @Schema(description = "退税金额")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount taxRefundPrice;

    @Schema(description = "采购合同编号")
    private String purchaseContractCode;

    @Schema(description = "采购员id")
    private Long purchaseUserId;

    @Schema(description = "采购员姓名")
    private String purchaseUserName;

    @Schema(description = "采购员部门id")
    private Long purchaseUserDeptId;

    @Schema(description = "采购员部门名称")
    private String purchaseUserDeptName;

    @Schema(description = "采购币种")
    private String purchaseCurrency;

    @Schema(description = "采购单价")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount purchaseUnitPrice;

    @Schema(description = "含税含包装单价")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount purchaseTotalPrice;

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

    @Schema(description = "外箱装量")
    private Integer qtyPerOuterbox;

    @Schema(description = "内盒装量")
    private Integer qtyPerInnerbox;

    @Schema(description = "箱数")
    private Integer boxCount;

    @Schema(description = "总体积")
    private BigDecimal totalVolume;

    @Schema(description = "总净重")
    @TableField(typeHandler = JsonWeightTypeHandler.class)
    private JsonWeight totalNetweight;

    @Schema(description = "总毛重")
    @TableField(typeHandler = JsonWeightTypeHandler.class)
    private JsonWeight totalGrossweight;

    @Schema(description = "中文说明")
    private String description;

    @Schema(description = "英文说明")
    private String descriptionEng;

    @Schema(description = "仓库编号")
    private String stockCode;

    @Schema(description = "仓库名称")
    private String stockName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "佣金金额")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount commissionAmount;

    @Schema(description = "毛利率")
    private BigDecimal orderGrossProfitRate;

    @Schema(description = "外销合同编号")
    private String saleContractCode;

    @Schema(description = "客户PO号")
    private String custPo;

    @Schema(description = "客户主键")
    private Long custId;

    @Schema(description = "客户编号")
    private String custCode;

    @Schema(description = "客户名称")
    private String custName;

    @Schema(description = "收款方式主键")
    private Long settlementId;

    @Schema(description = "收款方式名称")
    private String settlementName;

    @Schema(description = "价格条款")
    private String settlementTermType;

    @Schema(description = "发货地点")
    private Integer shippedAddress;

    @Schema(description = "交货日期")
    private LocalDateTime deliveryDate;

    @Schema(description = "佣金类型")
    private Integer commissionType;

    @Schema(description = "佣金比例")
    private BigDecimal commissionRate;

    @Schema(description = "包装价")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount purchasePackagingPrice;

    @Schema(description = "运费")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount purchaseShippingPrice;

    @Schema(description = "含税单价")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount purchaseWithTaxPrice;

    @Schema(description = "销售数量")
    private Integer saleQuantity;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "是否转出运明细")
    private Integer transformShipmentFlag;

    @Schema(description = "出运数量")
    private Integer shippingQuantity;

    @Schema(description = "跟单员")
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept manager;

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


    @Schema(description = "应付供应商名称")
    private String payVenderName;

    @Schema(description = "应付供应商id")
    private String payVenderId;

    @Schema(description = "应付供应商code")
    private String payVenderCode;

    /**
     * 外销币别
     */
    @Schema(description = "外销币别")
    private String currency;

    @Schema(description = "销售明细主键")
    private Long saleContractItemId;

    /**
     *
     * 海关计量单位
     */
    private String hsMeasureUnit;

    /**
     * 唯一标识
     */
    private String uniqueCode;

    /**
     * 来源编号
     */
    private String sourceUniqueCode;
    /**
     * 已出运数
     */
    @TableField(exist = false)
    private Integer shippedQuantity;

    /**
     * 未出运数
     */
    @TableField(exist = false)
    private Integer unshippedQuantity;

    /**
     * 业务员
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept sales;

    /**
     * 真实锁定数量
     */
    private Integer realLockQuantity;



    @TableField(exist = false)
    private Integer skuDeletedFlag;

    /**
     * 单位
     */
    private Integer measureUnit;

    /**
     * 验货状态
     */
    @TableField(exist = false)
    private Integer checkStatus;

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
     * 下单主体主键
     */
    private Long companyId;
    /**
     * 下单主体名称
     */
    private String companyName;

    /**
     * 是否包含赠品
     */
    private Integer freeFlag;

    /**
     * 库存采购合同号
     */
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> stockPurchaseContractCodes;

    /**
     * 条形码
     *
     * @author 波波
     */
    @Schema(description = "条形码")
    private String barcode;
}
