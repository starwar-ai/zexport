package com.syj.eplus.module.sms.api.dto;

import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import com.baomidou.mybatisplus.annotation.TableField;
import com.sun.xml.bind.v2.TODO;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonFileTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 外销合同明细 DO
 *
 * @author ePlus
 */
@Data
public class SaleContractItemSaveDTO {

    /**
     * 主键
     */
    @CompareField(value = "主键")
    private Long id;

    /**
     * 序号
     */
    private Integer sortNum;
    /**
     * 合同id
     */
    @CompareField(value = "合同id")
    private Long contractId;

    /**
     * 合同编号
     */
    private String contractCode;

    /**
     * 产品编号
     */
    @CompareField(value = "产品编号")
    private String skuCode;

    /**
     * 中文品名
     */
    @CompareField(value = "中文品名")
    private String name;

    /**
     * 英文品名
     */
    @CompareField(value = "英文品名")
    private String nameEng;

    /**
     * 图片
     */
    @CompareField(value = "图片")
    @TableField(typeHandler = JsonFileTypeHandler.class)
    private SimpleFile mainPicture;

    /**
     * 客户货号
     */
    @CompareField(value = "客户货号")
    private String cskuCode;

    /**
     * 数量
     */
    @CompareField(value = "数量")
    private Integer quantity;

    /**
     * 销售单价
     */
    @CompareField(value = "销售单价")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount unitPrice;

    /**
     * 外销总金额
     */
    @CompareField(value = "外销总金额")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount totalSaleAmount;

    /**
     * 采购单价
     */
    @CompareField(value = "采购单价")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount purchaseUnitPrice;

    /**
     * 真实采购单价
     */
    @TableField(exist = false, typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount realPurchaseWithTaxPrice;

    /**
     * 包装价
     */
    @CompareField(value = "包装价")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount purchasePackagingPrice;

    /**
     * 运费
     */
    @CompareField(value = "运费")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount purchaseShippingPrice;

    /**
     * 含税总价
     */
    @CompareField(value = "含税总价")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount purchaseWithTaxPrice;

    /**
     * 采购币种
     */
    @CompareField(value = "采购币种")
    private String purchaseCurrency;

    /**
     * 供应商名称
     */
    @CompareField(value = "供应商名称")
    private String venderName;

    /**
     * 供应商id
     */
    @CompareField(value = "供应商id")
    private String venderId;

    /**
     * 供应商code
     */
    @CompareField(value = "供应商code")
    private String venderCode;

    /**
     * 佣金类型
     * 枚举 {@link TODO commission_type 对应的类}
     */
    @CompareField(value = "佣金类型")
    private Integer commissionType;

    /**
     * 佣金比例
     */
    @CompareField(value = "佣金比例")
    private BigDecimal commissionRate;

    /**
     * 佣金金额
     */
    @CompareField(value = "佣金金额")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount commissionAmount;

    /**
     * 库存
     */
    @CompareField(value = "库存")
    private Integer inventoryQuantity;
 

    /**
     * 锁定数量
     */
    @CompareField(value = "锁定数量")
    private Integer currentLockQuantity;

    /**
     * 待采购数量
     */
    @CompareField(value = "待采购数量")
    private Integer needPurQuantity;

    /**
     * 单位
     */
    @CompareField(value = "单位")
    private String unit;

    /**
     * 中文说明
     */
    @CompareField(value = "中文说明")
    private String description;

    /**
     * 英文说明
     */
    @CompareField(value = "英文说明")
    private String descriptionEng;

    /**
     * 订单毛利
     */
    @CompareField(value = "订单毛利")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount orderGrossProfit;

    /**
     * 毛利率
     */
    @CompareField(value = "毛利率")
    private BigDecimal orderGrossProfitRate;

    /**
     * 工厂交期
     */
    @CompareField(value = "工厂交期")
    private LocalDateTime venderDeliveryDate;

    /**
     * 外箱装量
     */
    @CompareField(value = "外箱装量")
    private Integer qtyPerOuterbox;

    /**
     * 内盒装量
     */
    @CompareField(value = "内盒装量")
    private Integer qtyPerInnerbox;

    /**
     * 箱数
     */
    @CompareField(value = "箱数")
    private Integer boxCount;

    /**
     * 体积
     */
    @CompareField(value = "体积")
    private BigDecimal volume;


    /**
     * 是否翻单
     */
    @CompareField(value = "是否翻单")
    private Integer reorderFlag;

    /**
     * 海关编码
     */
    @CompareField(value = "海关编码")
    private String hsCode;

    /**
     * 包装方式
     */
    @CompareField(value = "包装方式")
    private List<Long> packageType;

    /**
     * 退税率
     */
    @CompareField(value = "退税率")
    private BigDecimal taxRefundRate;

    /**
     * 退税金额
     */
    @CompareField(value = "退税金额")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount taxRefundPrice;

    /**
     * 验货费用
     */
    @CompareField(value = "验货费用")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount inspectionFee;

    /**
     * 预付款金额
     */
    @CompareField(value = "预付款金额")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount prepayAmount;

    /**
     * 资金占用费
     */
    @CompareField(value = "资金占用费")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount fundOccupancyFee;

    /**
     * 拖柜费
     */
    @CompareField(value = "拖柜费")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount trailerFee;

    /**
     * 是否订舱
     */
    @CompareField(value = "是否订舱")
    private Integer bookingFlag;

    /**
     * 保险费
     */
    @CompareField(value = "保险费")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount insuranceFee;

    /**
     * 平台费
     */
    @CompareField(value = "平台费")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount platformFee;

    /**
     * 预估总费用
     */
    @CompareField(value = "预估总费用")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount forecastTotalCost;

    /**
     * 内部核算单价
     */
    @CompareField(value = "内部核算单价")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount innerCalcPrice;

    /**
     * 中信保费用
     */
    @CompareField(value = "中信保费用")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount sinosureFee;

    /**
     * 状态
     */
    @CompareField(value = "状态")
    private Integer status;

    /**
     * 采购员
     */
    @CompareField(value = "采购员")
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept purchaseUser;

    /**
     * 变更标志
     */
    @TableField(exist = false)
    private Integer changeFlag;

    /**
     * 产品主键
     */
    private Long skuId;

    /**
     * 是否自营
     */
    private Integer ownBrandFlag;
    /**
     * 客户产品标识
     */
    private Integer custProFlag;

    /**
     * 可选采购员
     */
    @TableField(exist = false)
    private List<UserDept> purchaseUserList;

    /**
     * 客户名称
     */
    @CompareField(value = "客户名称")
    private String custName;

    /**
     * 客户id
     */
    @CompareField(value = "客户id")
    private String custId;

    /**
     * 客户code
     */
    @CompareField(value = "客户code")
    private String custCode;

    /**
     * 内部法人单位id
     */
    @TableField(exist = false)
    private Long companyId;

    /**
     * 内部法人单位名称
     */
    @TableField(exist = false)
    private String companyName;

    /**
     * 产品类型
     */
    private Integer skuType;

    /**
     * 是否商检
     */
    private Integer commodityInspectionFlag;

    /**
     * 已出运数
     */
    @CompareField(value = "已出运数")
    private Integer shippedQuantity;

    /**
     * 未出运数
     */
    @CompareField(value = "未出运数")
    private Integer unshippedQuantity;


    private String sourceCode;

    private List<JsonEffectRange> effectRangeList;

    private String uniqueCode;

    /**
     * 入库状态
     */
    private Integer billStatus;

    /**
     * 规格
     */
    /**
     * 规格
     */
    private List<JsonSpecificationEntity> specificationList;

    /**
     * 是否分箱
     */
    private Integer splitBoxFlag;

    /**
     * 条形码
     *
     * @author 波波
     */
    private String barcode;
}
