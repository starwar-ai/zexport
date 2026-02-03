package com.syj.eplus.module.sms.dal.dataobject.salecontractitem;

import cn.iocoder.yudao.framework.common.annotations.ChangeIgnore;
import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.mybatis.core.exinterface.ChangeExInterface;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.config.handler.*;
import com.syj.eplus.framework.common.entity.*;
import com.syj.eplus.module.wms.api.stock.dto.StockDetailRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 外销合同明细 DO
 *
 * @author ePlus
 */

@TableName(value = "sms_sale_contract_item", autoResultMap = true)
@KeySequence("sms_sale_contract_item_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = false)
public class SaleContractItem extends BaseDO implements ChangeExInterface {

    /**
     * 主键
     */
    @TableId
    @ChangeIgnore
    private Long id;

    /**
     * 序号
     */
    @ChangeIgnore
    private Integer sortNum;
    /**
     * 合同id
     */
    @ChangeIgnore
    private Long contractId;

    /**
     * 合同编号
     */
    @ChangeIgnore
    private String contractCode;

    /**
     * 产品编号
     */
    @ChangeIgnore
    private String skuCode;

    /**
     * 基础产品编号
     */
    @ChangeIgnore
    private String basicSkuCode;

    /**
     * 中文品名
     */
    @ChangeIgnore
    private String name;

    /**
     * 英文品名
     */
    @ChangeIgnore
    private String nameEng;

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
     * 客户货号
     */
    @ChangeIgnore
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
    @ChangeIgnore
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount totalSaleAmount;

    /**
     * 采购单价
     */
    @ChangeIgnore
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount purchaseUnitPrice;

    /**
     * 真实采购单价
     */
    @ChangeIgnore
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount realPurchaseWithTaxPrice;

    /**
     * 包装价
     */
    @ChangeIgnore
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount purchasePackagingPrice;

    /**
     * 运费
     */
    @ChangeIgnore
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount purchaseShippingPrice;

    /**
     * 含税单价
     */
    @ChangeIgnore
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount purchaseWithTaxPrice;

    /**
     * 含税含包装单价
     */
    @ChangeIgnore
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount purchaseTotalPrice;

    /**
     * 采购币种
     */
    @ChangeIgnore
    private String purchaseCurrency;

    /**
     * 供应商名称
     */
    @ChangeIgnore
    private String venderName;

    /**
     * 供应商id
     */
    @ChangeIgnore
    private Long venderId;

    /**
     * 供应商code
     */
    @ChangeIgnore
    private String venderCode;

    /**
     * 佣金类型
     * TODO: commission_type 对应的枚举类
     */
    private Integer commissionType;

    /**
     * 佣金比例
     */
    private BigDecimal commissionRate;

    /**
     * 佣金是否扣减总金额
     */
    private Integer commissionSubTotal;

    /**
     * 佣金金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount commissionAmount;

    /**
     * 库存
     */
    @ChangeIgnore
    private Integer inventoryQuantity;

    /**
     * 锁定数量
     */
    private Integer currentLockQuantity;

    /**
     * 真实锁定数量
     */
    @ChangeIgnore
    private Integer realLockQuantity;

    /**
     * 真实采购数量
     */
    @ChangeIgnore
    private Integer realPurchaseQuantity;

    /**
     * 待采购数量
     */
    private Integer needPurQuantity;

    /**
     * 单位
     */
    @ChangeIgnore
    private String unit;

    /**
     * 中文说明
     */
    @ChangeIgnore
    private String description;

    /**
     * 英文说明
     */
    @ChangeIgnore
    private String descriptionEng;

    /**
     * 订单毛利
     */
    @ChangeIgnore
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount orderGrossProfit;

    /**
     * 毛利率
     */
    @ChangeIgnore
    private BigDecimal orderGrossProfitRate;

    /**
     * 工厂交期
     */
    @ChangeIgnore
    private LocalDateTime venderDeliveryDate;

    /**
     * 外箱装量
     */
    @ChangeIgnore
    private Integer qtyPerOuterbox;

    /**
     * 内盒装量
     */
    @ChangeIgnore
    private Integer qtyPerInnerbox;

    /**
     * 箱数
     */
    @ChangeIgnore
    private Integer boxCount;

    /**
     * 体积
     */
    @ChangeIgnore
    private BigDecimal volume;

    /**
     * 是否翻单
     */
    @ChangeIgnore
    private Integer reorderFlag;

    /**
     * 海关编码
     */
    @ChangeIgnore
    private String hsCode;

    /**
     * 包装方式
     */
    @ChangeIgnore
    @TableField(typeHandler = LongListTypeHandler.class)
    private List<Long> packageType;

    /**
     * 包装方式名称
     */
    @ChangeIgnore
    @TableField(exist = false)
    private String packageTypeName;

    /**
     * 包装方式英文名称
     */
    @TableField(exist = false)
    @ChangeIgnore
    private String packageTypeEngName;

    /**
     * 退税率
     */
    @ChangeIgnore
    private BigDecimal taxRefundRate;

    /**
     * 退税率
     */
    @ChangeIgnore
    private BigDecimal realTaxRefundRate;

    /**
     * 退税金额
     */
    @ChangeIgnore
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount taxRefundPrice;

    /**
     * 验货费用
     */
    @ChangeIgnore
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount inspectionFee;

    /**
     * 资金占用费
     */
    @ChangeIgnore
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount fundOccupancyFee;

    /**
     * 拖柜费
     */
    @ChangeIgnore
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount trailerFee;

    /**
     * 是否订舱
     */
    @ChangeIgnore
    private Integer bookingFlag;

    /**
     * 保险费
     */
    @ChangeIgnore
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount insuranceFee;

    /**
     * 平台费
     */
    @ChangeIgnore
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount platformFee;

    /**
     * 预估总费用
     */
    @ChangeIgnore
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount forecastTotalCost;

    /**
     * 内部核算单价
     */
    @ChangeIgnore
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount innerCalcPrice;

    /**
     * 中信保费用
     */
    @ChangeIgnore
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount sinosureFee;

    /**
     * 状态
     */
    @ChangeIgnore
    private Integer status;

    /**
     * 采购员
     */
    @ChangeIgnore
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
    @ChangeIgnore
    private Long skuId;

    /**
     * 是否自营
     */
    @ChangeIgnore
    private Integer ownBrandFlag;
    /**
     * 客户产品标识
     */
    @ChangeIgnore
    private Integer custProFlag;

    /**
     * 可选采购员
     */
    @ChangeIgnore
    @TableField(exist = false)
    private List<UserDept> purchaseUserList;

    /**
     * 内部法人单位id
     */
    @ChangeIgnore
    @TableField(exist = false)
    private Long companyId;

    /**
     * 内部法人单位名称
     */
    @ChangeIgnore
    @TableField(exist = false)
    private String companyName;

    /**
     * 产品类型
     */
    @ChangeIgnore
    private Integer skuType;

    /**
     * 是否商检
     */
    @ChangeIgnore
    private Integer commodityInspectionFlag;

    /**
     * 已出运数
     */
    @ChangeIgnore
    private Integer shippedQuantity;

    /**
     * 已转出运数
     */
    @ChangeIgnore
    private Integer transferShippedQuantity;

    @Schema(description = "库存锁定-请求参数")
    @ChangeIgnore
    @TableField(typeHandler = JsonsTockLockSaveReqTypeHandler.class)
    private List<StockLockSaveReqVO> stockLockSaveReqVOList;

    @Schema(description = "库存锁定-响应参数")
    @TableField(exist = false)
    @ChangeIgnore
    private List<StockDetailRespVO> stockDetailRespVOList;

    @TableField(exist = false)
    @ChangeIgnore
    private String sourceCode;

    @TableField(exist = false)
    @ChangeIgnore
    private List<JsonEffectRange> effectRangeList;

    /**
     * 唯一编号
     */
    @ChangeIgnore
    private String uniqueCode;


    /**
     * 来源编号
     */
    @ChangeIgnore
    private String sourceUniqueCode;


    /**
     * 海关计量单位
     */
    @ChangeIgnore
    private String hsMeasureUnit;

    /**
     * 入库状态
     */
    @ChangeIgnore
    private Integer billStatus;

    /**
     * 异常状态
     */
    @ChangeIgnore
    private Integer abnormalStatus;

    /**
     * 异常说明
     */
    @ChangeIgnore
    private String abnormalRemark;

    /**
     * 已入库数量
     */
    @ChangeIgnore
    private Integer billQuantity;

    /**
     * 转出运标识：0-否 1-是
     */
    @ChangeIgnore
    private Integer convertShipmentFlag;

    @CompareField(value = "跟单员")
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept manager;

    @ChangeIgnore
    private Integer shipmentTotalQuantity;


    @ChangeIgnore
    @TableField(exist = false)
    private Integer skuDeletedFlag;

    /**
     * 转采购标识
     */
    @ChangeIgnore
    private Integer convertPurchaseFlag;

    /**
     * 锁库信息
     */
    @ChangeIgnore
    @TableField(typeHandler = JsonLockListTypeHandler.class)
    private List<JsonLock>  lockMsg;

    /**
     * 重新锁库标记
     * 可以重新分配：0  不可以：1
     */
    @ChangeIgnore
    private Integer reLockFlag;

    /**
     * 内部法人单位id
     */
    @TableField(exist = false)
    @ChangeIgnore
    private List<Long> companyIdList;

    /**
     * 税率
     */
    @ChangeIgnore
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

    @TableField(exist = false)
    private Long purchaseCompanyId;

    @TableField(exist = false)
    private Long purchaseUserId;

    /**
     * 锁库单价
     */
    @TableField(exist = false)
    private JsonAmount stockLockPrice;

    /**
     * 锁库总价
     */
    @TableField(exist = false)
    private JsonAmount stockLockTotalPrice;

    /**
     * 转出库通知单标识
     */
    private Integer converNoticeFlag;

    /**
     * 采购合同号
     */
    @TableField(exist = false)
    private String purchaseContractCode;

    /**
     * 业务员
     */
    @TableField(exist = false)
    @ChangeIgnore
    private UserDept sales;

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
     * 真实采购币种
     */
    @TableField(exist = false)
    @ChangeIgnore
    private String realPrchaseCurrency;


    /**
     * 真实包装价
     */
    @TableField(exist = false)
    @ChangeIgnore
    private JsonAmount realPackagingPrice;

    /**
     * 真实运费
     */
    @TableField(exist = false)
    @ChangeIgnore
    private JsonAmount realShippingPrice;

    /**
     * 真实规格
     */
    @TableField(exist = false)
    @ChangeIgnore
    private List<JsonSpecificationEntity> realSpecificationList;

    /**
     * 真实是否分箱
     */
    @TableField(exist = false)
    @ChangeIgnore
    private Integer realSplitBoxFlag;

    /**
     * 真实供应商id
     */
    @TableField(exist = false)
    @ChangeIgnore
    private Long realVenderId;

    /**
     * 真实供应商名称
     */
    @TableField(exist = false)
    @ChangeIgnore
    private String realVenderName;
    /**
     * 真实供应商编号
     */
    @TableField(exist = false)
    @ChangeIgnore
    private String realVenderCode;

    /**
     * 真实退税金额
     */
    @ChangeIgnore
    @TableField(exist = false)
    private JsonAmount realTaxRefundPrice;

    /**
     * 真实箱数
     */
    @TableField(exist = false)
    private Integer realBoxCount;

    /**
     * 是否包含赠品
     */
    private Integer freeFlag;

    /**
     * 采购赠品数量
     */
    private Integer purchaseFreeQuantity;

    /**
     * 不包含赠品采购价
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount withTaxPriceRemoveFree;

    /**
     * 同步标记
     */
    private  long syncCode;

    /**
     * 自营产品货号
     */
    private String oskuCode;

    /**
     * 条形码
     *
     * @author 波波
     */
    private String barcode;


    /**
     * 外销总金额USD
     */
    @ChangeIgnore
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount totalSaleAmountUsd;
}
