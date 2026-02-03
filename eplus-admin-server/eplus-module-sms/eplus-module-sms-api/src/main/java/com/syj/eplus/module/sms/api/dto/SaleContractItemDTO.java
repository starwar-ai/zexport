package com.syj.eplus.module.sms.api.dto;

import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import com.baomidou.mybatisplus.annotation.TableField;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonSplitPurchaseListTypeHandler;
import com.syj.eplus.framework.common.entity.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Data
public class SaleContractItemDTO {

    private Long id;

    /**
     * 序号
     */
    private Integer sortNum;
    /**
     * 合同id
     */
    private Long contractId;

    /**
     * 合同编号
     */
    private String contractCode;

    /**
     * 产品编号
     */
    private String skuCode;

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
    private SimpleFile mainPicture;

    /**
     * 客户货号
     */
    private String cskuCode;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 销售单价
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount unitPrice;

    /**
     * 外销总金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount totalSaleAmount;

    /**
     * 采购单价
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount purchaseUnitPrice;

    /**
     * 真实采购单价
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount realPurchaseWithTaxPrice;

    /**
     * 包装价
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount purchasePackagingPrice;

    /**
     * 运费
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount purchaseShippingPrice;

    /**
     * 含税单价
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount purchaseWithTaxPrice;

    /**
     * 采购币种
     */
    private String purchaseCurrency;

    /**
     * 供应商名称
     */
    private String venderName;

    /**
     * 供应商id
     */
    private Long venderId;

    /**
     * 供应商code
     */
    private String venderCode;

    private Integer commissionType;

    /**
     * 佣金比例
     */
    private BigDecimal commissionRate;

    /**
     * 佣金金额
     */
    private JsonAmount commissionAmount;

    /**
     * 库存
     */
    private Integer inventoryQuantity;

    /**
     * 锁定数量
     */
    private Integer currentLockQuantity;
    /**
     * 真实锁定数量
     */
    private Integer realLockQuantity;

    /**
     * 真实采购数量
     */
    private Integer realPurQuantity;

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
    private JsonAmount orderGrossProfit;

    /**
     * 毛利率
     */
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
    private Integer boxCount;

    /**
     * 体积
     */
    private BigDecimal volume;

    /**
     * 是否翻单
     */
    private Integer reorderFlag;

    /**
     * 海关编码
     */
    private String hsCode;

    /**
     * 包装方式
     */
    private List<Long> packageType;

    /**
     * 包装方式名称
     */
    private String packageTypeName;

    /**
     * 包装方式英文名称
     */
    private String packageTypeEngName;

    /**
     * 退税率
     */
    private BigDecimal taxRefundRate;

    /**
     * 退税金额
     */
    private JsonAmount taxRefundPrice;

    /**
     * 验货费用
     */
    private JsonAmount inspectionFee;

    /**
     * 资金占用费
     */
    private JsonAmount fundOccupancyFee;

    /**
     * 拖柜费
     */
    private JsonAmount trailerFee;

    /**
     * 是否订舱
     */
    private Integer bookingFlag;

    /**
     * 保险费
     */
    private JsonAmount insuranceFee;

    /**
     * 平台费
     */
    private JsonAmount platformFee;

    /**
     * 预估总费用
     */
    private JsonAmount forecastTotalCost;

    /**
     * 内部核算单价
     */
    private JsonAmount innerCalcPrice;

    /**
     * 中信保费用
     */
    private JsonAmount sinosureFee;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 采购员
     */
    private UserDept purchaseUser;

    /**
     * 变更标志
     */
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
    private String custName;

    /**
     * 客户id
     */
    private Long custId;

    /**
     * 客户code
     */
    private String custCode;

    /**
     * 内部法人单位id
     */
    private Long companyId;

    /**
     * 内部法人单位名称
     */
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
    private Integer shippedQuantity;

    /**
     * 未出运数
     */
    private Integer unshippedQuantity;



    @TableField(exist = false)
    private String sourceCode;

    @TableField(exist = false)
    private List<JsonEffectRange> effectRangeList;

    /**
     * 唯一编号
     */
    private String uniqueCode;


    /**
     * 来源编号
     */
    private String sourceUniqueCode;


    /**
     * 海关计量单位
     */
    private String hsMeasureUnit;

    /**
     * 入库状态
     */
    private Integer billStatus;

    /**
     * 异常状态
     */
    private Integer abnormalStatus;

    /**
     * 异常说明
     */
    private String abnormalRemark;

    /**
     * 已入库数量
     */
    private Integer billQuantity;

    /**
     * 转出运标识：0-否 1-是
     */
    private Integer convertShipmentFlag;

    private UserDept manager;

    private Integer shipmentTotalQuantity;

    private Integer ReLockFlg;

    private List<JsonLock> lockMsg;

    /**
     * 重新锁库标记
     * 可以重新分配：0  不可以：1
     */
    private Integer reLockFlag;

    /**
     * 税率
     */
    private BigDecimal taxRate;

    /**
     * 计量单位
     */
    private Integer measureUnit;

    /**
     * 拆分标识
     */
    private Integer splitFlag;

    /**
     * 是否拆分采购
     */
    private Integer splitPurchaseFlag;


    /**
     * 拆分采购数量
     */
    private Integer splitPurchaseQuantity;


    /**
     * 拆分采购信息
     */
    @TableField(typeHandler = JsonSplitPurchaseListTypeHandler.class)
    private List<SplitPurchase> splitPurchaseList;

    /**
     * 总体积(单箱体积x箱数cm³)
     */
    private BigDecimal totalVolume;
    /**
     * 总毛重（单箱毛重x箱数 {数量,单位}）
     */
    private JsonWeight totalWeight;

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
     * 业务员
     */
    private UserDept sales;

    /**
     * 销售类型
     */
    private Integer saleType;

    /**
     * 条形码
     *
     * @author 波波
     */
    private String barcode;
}
