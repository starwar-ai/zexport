package com.syj.eplus.module.sms.dal.dataobject.salecontractchange;

import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import cn.iocoder.yudao.framework.mybatis.core.exinterface.ChangeExInterface;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.sun.xml.bind.v2.TODO;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonFileTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/1 16:29
 */
@Data
@Accessors(chain = false)
@Schema(description = "销售明细")
public class SaleContractItemChange implements ChangeExInterface {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 合同id
     */
    private Long contractId;
    /**
     * 产品编号
     */
    private String skuCode;

    /**
     * 基础产品编号
     */
    private String basicSkuCode;
    /**
     * 中文品名
     */
    private String name;
    /**
     * 英文品名
     */
    private String nameEng;
    /**
     * 图片
     */
    @TableField(typeHandler = JsonFileTypeHandler.class)
    private SimpleFile mainPicture;
    /**
     * 客户货号
     */
    private String cskuCode;
    /**
     * 数量
     */
    @CompareField
    private Integer quantity;
    /**
     * 销售单价
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    @CompareField
    private JsonAmount unitPrice;
    /**
     * 外销总金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    @CompareField
    private JsonAmount totalSaleAmount;
    /**
     * 采购单价
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount unitCost;
    /**
     * 采购单价(含包装)
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount unitCostRate;
    /**
     * 采购总金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount totalPurchaseAmount;
    /**
     * 采购币种
     */
    private String purchaseCurrency;
    /**
     * 厂商名称
     */
    private String supplierName;
    /**
     * 佣金类型
     * 枚举 {@link TODO commission_type 对应的类}
     */
    @CompareField
    private Integer commissionType;
    /**
     * 佣金比例
     */
    @CompareField
    private BigDecimal commissionRate;
    /**
     * 佣金金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    @CompareField
    private JsonAmount commissionAmount;
    /**
     * 库存
     */
    @CompareField
    private Integer inventoryQuantity;
    /**
     * 采购数量
     */
    @CompareField
    private Integer purchaseQuantity;

    /**
     * 单位
     */
    private String unit;

    /**
     * 中文说明
     */
    private String description;

    /**
     * 英文说明
     */
    private String descriptionEng;

    /**
     * 订单毛利
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    @CompareField
    private JsonAmount orderGrossProfit;

    /**
     * 毛利率
     */
    @CompareField
    private BigDecimal orderGrossProfitRate;

    /**
     * 工厂交期
     */
    private LocalDateTime venderDeliveryDate;

    /**
     * 外箱装量
     */
    private Integer qtyPerOuterbox;

    /**
     * 内盒装量
     */
    private Integer qtyPerInnerbox;

    /**
     * 箱数
     */
    @CompareField
    private Integer boxCount;

    /**
     * 体积
     */
    @CompareField
    private BigDecimal volume;

    /**
     * 是否翻单
     */
    private Boolean reorderFlag;

    /**
     * 采购员名称
     */
    @CompareField
    private String purchaserName;

    /**
     * 海关编码
     */
    private String hsCode;

    /**
     * 包装方式
     */
    private List<Long> packageType;

    /**
     * 退税率
     */
    private BigDecimal taxRefundRate;

    /**
     * 退税金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    @CompareField
    private JsonAmount taxRefundPrice;

    /**
     * 已出货数
     */
    private Integer expectCount;

    /**
     * 未发货数
     */
    private Integer quantityShipped;

    /**
     * 验货费用
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount inspectionFee;

    /**
     * 预付款金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount prepayAmount;

    /**
     * 资金占用费
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount fundOccupancyFee;

    /**
     * 拖柜费
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    @CompareField
    private JsonAmount trailerFee;

    /**
     * 是否订舱
     */
    private Integer bookingFlag;

    /**
     * 保险费
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount insuranceFee;

    /**
     * 平台费
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    @CompareField
    private JsonAmount platformFee;

    /**
     * 预估总费用
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    @CompareField
    private JsonAmount forecastTotalCost;

    /**
     * 内部核算单价
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount innerCalcPrice;

    /**
     * 中信保费用
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount sinosureFee;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 采购员
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    @CompareField
    private UserDept purchaseUser;

    private Integer changeFlag;

    /**
     * 含税总价
     */
    @CompareField(value = "含税总价")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount purchaseWithTaxPrice;
    /**
     * 旧产品编号
     */
    private String old_skuCode;

    /**
     * 旧基础产品编号
     */
    private String old_basicSkuCode;
    /**
     * 旧中文品名
     */
    private String old_name;
    /**
     * 旧英文品名
     */
    private String old_nameEng;
    /**
     * 旧图片
     */
    @TableField(typeHandler = JsonFileTypeHandler.class)
    private SimpleFile old_mainPicture;
    /**
     * 旧客户货号
     */
    private String old_cskuCode;
    /**
     * 旧数量
     */
    private Integer old_quantity;
    /**
     * 旧销售单价
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount old_unitPrice;
    /**
     * 旧外销总金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount old_totalSaleAmount;
    /**
     * 旧采购单价
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount old_unitCost;
    /**
     * 旧采购单价(含包装)
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount old_unitCostRate;
    /**
     * 旧采购总金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount old_totalPurchaseAmount;
    /**
     * 旧采购币种
     */
    private String old_purchaseCurrency;
    /**
     * 旧厂商名称
     */
    private String old_supplierName;
    /**
     * 旧佣金类型
     * 枚举 {@link TODO commission_type 对应的类}
     */
    private Integer old_commissionType;
    /**
     * 旧佣金比例
     */
    private BigDecimal old_commissionRate;
    /**
     * 旧佣金金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount old_commissionAmount;
    /**
     * 旧库存
     */
    private Integer old_inventoryQuantity;
    /**
     * 旧采购数量
     */
    private Integer old_purchaseQuantity;

    /**
     * 旧单位
     */
    private String old_unit;

    /**
     * 旧中文说明
     */
    private String old_description;

    /**
     * 旧英文说明
     */
    private String old_descriptionEng;

    /**
     * 旧订单毛利
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount old_orderGrossProfit;

    /**
     * 旧毛利率
     */
    private BigDecimal old_orderGrossProfitRate;

    /**
     * 旧工厂交期
     */
    private LocalDateTime old_venderDeliveryDate;

    /**
     * 旧外箱装量
     */
    private Integer old_qtyPerOuterbox;

    /**
     * 旧内盒装量
     */
    private Integer old_qtyPerInnerbox;

    /**
     * 旧箱数
     */
    private Integer old_boxCount;

    /**
     * 旧体积
     */
    private BigDecimal old_volume;

    /**
     * 旧是否翻单
     */
    private Boolean old_reorderFlag;

    /**
     * 旧采购员名称
     */
    private String old_purchaserName;

    /**
     * 旧海关编码
     */
    private String old_hsCode;

    /**
     * 旧包装方式
     */
    private Integer old_packageType;

    /**
     * 旧退税率
     */
    private BigDecimal old_taxRefundRate;

    /**
     * 旧退税金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount old_taxRefundPrice;

    /**
     * 旧已出货数
     */
    private Integer old_expectCount;

    /**
     * 旧未发货数
     */
    private Integer old_quantityShipped;

    /**
     * 旧验货费用
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount old_inspectionFee;

    /**
     * 旧预付款金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount old_prepayAmount;

    /**
     * 旧资金占用费
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount old_fundOccupancyFee;

    /**
     * 旧拖柜费
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount old_trailerFee;

    /**
     * 旧是否订舱
     */
    @CompareField
    private Integer old_bookingFlag;

    /**
     * 旧保险费
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    @CompareField
    private JsonAmount old_insuranceFee;

    /**
     * 旧平台费
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount old_platformFee;

    /**
     * 旧预估总费用
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount old_forecastTotalCost;

    /**
     * 旧内部核算单价
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount old_innerCalcPrice;

    /**
     * 旧中信保费用
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount old_sinosureFee;

    /**
     * 旧状态
     */
    private Integer old_status;

    /**
     * 旧采购员
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept old_purchaseUser;

    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount old_purchaseWithTaxPrice;

    @TableField(exist = false)
    private String sourceCode;

    @TableField(exist = false)
    private List<JsonEffectRange> effectRangeList;

    @Schema(description = "规格")
    /**
     * 规格
     */
    private List<JsonSpecificationEntity> specificationList;

    /**
     * 是否分箱
     */
    private Integer splitBoxFlag;

    private Integer old_splitBoxFlag;

    @Schema(description = "旧规格")
    private List<JsonSpecificationEntity> old_specificationList;

    /**
     * 是否包含赠品
     */
    private Integer freeFlag;

    /**
     * 赠品数量
     */
    private Integer freeQuantity;

    /**
     * 采购赠品数量
     */
    private Integer purchaseFreeQuantity;
}
