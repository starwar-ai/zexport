package com.syj.eplus.module.scm.dal.dataobject.purchasecontractitem;

import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.mybatis.core.exinterface.ChangeExInterface;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.config.handler.*;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.SimpleFile;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 采购合同明细 DO
 *
 * @author zhangcm
 */

@TableName(value = "scm_purchase_contract_item", autoResultMap = true)
@KeySequence("scm_purchase_contract_item_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = false)
public class PurchaseContractItemDO extends BaseDO implements ChangeExInterface {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 版本
     */
    private Integer ver;
    /**
     * 序号
     */
    private Integer sortNum;
    /**
     * 产品skuId
     */
    private Long skuId;
    /**
     * 产品编号
     */
    private String skuCode;
    /**
     * 产品名称
     */
    private String skuName;
    /**
     * 客户ID
     */
    private Long custId;
    /**
     * 客户编号
     */
    private String custCode;
    /**
     * 客户名称
     */
    private String custName;
    /**
     * 主图
     */
    @TableField(typeHandler = JsonFileTypeHandler.class)
    private SimpleFile mainPicture;
    /**
     * 缩略图
     */
    private String thumbnail;
    /**
     * 包装方式名称
     */
    private String packageTypeName;
    /**
     * 包装方式英文名称
     */
    private String packageTypeEngName;
    /**
     * 客户货号
     */
    private String cskuCode;

    /**
     * 基础产品编号
     */
    @CompareField(value = "基础产品编号")
    private String basicSkuCode;

    /**
     * 采购数量
     */
    @CompareField(value = "采购数量")
    private Integer quantity;

    /**
     * 供应商id
     */
    private Long venderId;
    /**
     * 供应商编号
     */
    private String venderCode;
    /**
     * 交货日期
     */
    private LocalDateTime planArriveDate;
    /**
     * 验货状态
     */
    private Integer checkStatus;
    /**
     * 已验货数量
     */
    private Integer checkedQuantity;
    /**
     * 收货状态
     */
    private Integer receiveStatus;
    /**
     * 已收货数量
     */
    private Integer receivedQuantity;
    /**
     * 退货量
     */
    private Integer exchangeQuantity;
    /**
     * 换货量
     */
    private Integer returnQuantity;
    /**
     * 采购合同单号
     */
    private Long purchaseContractId;
    /**
     * 采购合同编号
     */
    private String purchaseContractCode;
    /**
     * 内箱装量
     */
    private Integer qtyPerInnerbox;
    /**
     * 外箱装量
     */
    private Integer qtyPerOuterbox;
    /**
     * 包装规格长度
     */
    private BigDecimal packageLength;
    /**
     * 是否同步供应商
     */
    @CompareField(value = "是否同步供应商",enumType = "is_int")
    private Integer syncQuoteFlag;
    /**
     * 包装规格宽度
     */
    private BigDecimal packageWidth;
    /**
     * 包装规格高度
     */
    private BigDecimal packageHeight;
    /**
     * 包装规格单位
     */
    private Integer packageUnit;
    /**
     * 单品毛重
     */
    @TableField(typeHandler = JsonWeightTypeHandler.class)
    private JsonWeight singleGrossweight;
    /**
     * 包装价
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    @CompareField(value = "包装价")
    private JsonAmount packagingPrice;
    /**
     * 运费
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    @CompareField(value = "运费")
    private JsonAmount shippingPrice;
    /**
     * 采购单价
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    @CompareField(value = "采购单价")
    private JsonAmount unitPrice;
    /**
     * 含税含包装含运费单价
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount totalPrice;
    /**
     * 含税单价
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount withTaxPrice;
    /**
     * 含税总价（人民币）
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    @CompareField(value = "含税总价（人民币）")
    private JsonAmount totalPriceRmb;
    /**
     * 税率
     */
    private BigDecimal taxRate;
    /**
     * 采购类型
     */
    @CompareField(value = "采购类型",enumType = "purchase_type")
    private Integer purchaseType;
    /**
     * 采购员id
     */
    private Long purchaseUserId;
    /**
     * 采购员姓名
     */
    private String purchaseUserName;
    /**
     * 采购员部门id
     */
    private Long purchaseUserDeptId;
    /**
     * 采购员部门名称
     */
    private String purchaseUserDeptName;
    /**
     * 工厂货号
     */
    private String venderProdCode;
    /**
     * 报价日期
     */
    private LocalDateTime quoteDate;
    /**
     * 是否含运费
     */
    @CompareField(value = "是否含运费",enumType = "is_int")
    private Integer freightFlag;
    /**
     * 是否含包装
     */
    @CompareField(value = "是否含包装",enumType = "is_int")
    private Integer packageFlag;
    /**
     * 包装方式
     */
    @CompareField(value = "包装方式")
    @TableField(typeHandler = LongListTypeHandler.class)
    private List<Long> packageType;
    /**
     * 币种
     */
    private String currency;
    /**
     * 是否含包装
     */
    @CompareField(value = "是否含包装",enumType = "is_int")
    private Integer faxFlag;
    /**
     * 最小起购量
     */
    private Integer moq;
    /**
     * 箱数
     */
    private Integer boxCount;
    /**
     * 入库状态
     */
    private Integer warehousingType;
    /**
     * 采购合同回写仓库ids
     */
    private String wmsIds;
    /**
     * 采购合同回写仓库名称列表
     */
    private String wmsNames;
    /**
     * 是否赠品
     */
    @CompareField(value = "是否赠品",enumType = "is_int")
    private Integer freeFlag;
    /**
     * 交期
     */
    private Integer delivery;
    /**
     * 采购链接
     */
    private String purchaseUrl;
    /**
     * 备注
     */
    private String remark;

    /**
     * 是否通用辅料
     */
    private Integer auxiliarySkuFlag;

    /**
     * 辅料采购类型
     */
    private Integer auxiliarySkuType;

    /**
     * 规格描述
     */
    private String specRemark;

    /**
     * 附件
     */
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> annex;

    /**
     * 辅料属于的销售合同
     */
    private String auxiliarySaleContractCode;

    /**
     * 辅料属于的采购合同
     */
    private String auxiliaryPurchaseContractCode;
    /**
     * 辅料属于的采购合同明细id
     */
    private Long auxiliaryPurchaseContractItemId;

    /**
     * 辅料属于的采购合同产品id
     */
    private Long auxiliarySkuId;

    /**
     * 辅料属于的采购合同产品编号
     */
    private String auxiliarySkuCode;

    /**
     * 辅料属于的采购合同产品客户货号
     */
    private String auxiliaryCskuCode;

    /**
     * 验货费
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount checkCost;

    /**
     * 计量单位
     */
    private String skuUnit;
    /**
     * 自主品牌标记
     */
    private Integer ownBrandFlag;
    /**
     * 客户产品标记
     */
    private Integer custProFlag;

    /**
     * 包装费
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount packageCost;

    /**
     * 销售明细主键
     */
    private Long saleContractItemId;

    /**
     * 开票状态
     */
    private Integer invoiceStatus;

    /**
     * 已开票数量
     */
    private Integer invoicedQuantity;

    /**
     * 开票币种
     */
    private String invoicedCurrency;

    /**
     * 已开票金额
     */
    private BigDecimal invoicedAmount;

    /**
     * 已支付金额
     */
    private BigDecimal paymentAmount;

    @TableField(exist = false)
    private Integer changeFlag;

    /**
     * 已申请金额
     */

    private BigDecimal appliedAmount;

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
     * 已转入库通知单数量
     */
    private Integer noticedQuantity;

    /**
     * 入库状态
     */
    private Integer billStatus;

    /**
     * 入库数量
     */
    private Integer billQuantity;

    /**
     * 异常状态
     */
    private Integer abnormalStatus;

    /**
     * 异常说明
     */
    private String abnormalRemark;

    /**
     * 销售合同明细编号
     */
    private String saleItemUniqueCode;
    /**
     * 下推辅料采购合同标记
     */
    private Integer pushDownAuxiliaryFlag;
    /**
     * 登票状态
     */
    private Integer registerNoticeStatus;
    /**
     * 登票数量
     */
    private Integer registerNoticeQuantity;

    /**
     * 销售合同主键
     */
    private Long saleContractId;

    /**
     * 销售合同编码
     */
    private String saleContractCode;
    /**
     * 出运标识：0-否 1-是
     */
    private Integer outFlag;

    /**
     * 拆分主体id
     */
    private Long splitCompanyId;

    /**
     * 拆分主体名称
     */
    private String splitCompanyName;

    /**
     * 出库数量
     */
    private Integer outQuantity;

    /**
     * 作废标识
     */
    private Integer cancelFlag;

    /**
     * 产品类型
     */
    private Integer skuType;

    /**
     * 处理标识
     */
    private Integer handleFlag;

    /**
     * 验货时间
     */
    private LocalDateTime inspectionTime;

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
     * 包材采购计划合同数量
     */
    private Integer totalQuantity;

    /**
     * 赠品数量
     */
    private Integer freeQuantity;

    /**
     * 是否拆分采购
     */
    private Integer splitFlag;

    /**
     * 客户po号
     */
    private String custPo;

    /**
     * 描述
     */
    private String description;
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


}
