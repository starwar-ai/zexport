package com.syj.eplus.module.scm.api.purchaseplan.dto;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
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
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 采购计划明细 DO
 *
 * @author zhangcm
 */

@TableName(value = "scm_purchase_plan_item", autoResultMap = true)
@KeySequence("scm_purchase_plan_item_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchasePlanItemDTO extends BaseDO {

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
     * 一级拆分序号
     */
    private Integer oneSplitNum;

    /**
     * 二级拆分序号
     */
    private Integer twoSplitNum;

    /**
     * 三级拆分序号
     */
    private Integer threeSplitNum;

    /**
     * 组合产品父级主键
     */
    private Integer sourceId;

    /**
     * 明细层级
     */
    private Integer levels;
    /**
     * 产品id
     */
    private Long skuId;
    /**
     * 产品编号
     */
    @CompareField(value = "产品编号")
    private String skuCode;
    /**
     * 产品名称
     */
    private String skuName;

    /**
     * 图片
     */
    @CompareField(value = "图片")
    @TableField(typeHandler = JsonFileTypeHandler.class)
    private SimpleFile mainPicture;
    /**
     * 客户id
     */
    private Long custId;
    /**
     * 客户编号
     */
    @CompareField(value = "客户编号")
    private String custCode;
    /**
     * 客户货号
     */
    @CompareField(value = "客户货号")
    private String cskuCode;

    /**
     * 销售数量
     */
    @CompareField(value = "销售数量")
    private Integer saleQuantity;
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
     * 下单主体-主键
     */
    @CompareField(value = "下单主体-主键")
    private Long purchaseCompanyId;

    /**
     * 下单主体-名称
     */
    @CompareField(value = "下单主体-名称")
    private String purchaseCompanyName;

    /**
     * 供应商id
     */
    private Long venderId;
    /**
     * 供应商编号
     */
    @CompareField(value = "供应商编号")
    private String venderCode;
    /**
     * 仓库id
     */
    private Long stockId;
    /**
     * 仓库编号
     */
    @CompareField(value = "仓库编号")
    private String stockCode;
    /**
     * 采购计划id
     */
    private Long purchasePlanId;
    /**
     * 采购计划编号
     */
    @CompareField(value = "采购计划编号")
    private String purchasePlanCode;
    /**
     * 包装价
     */
    @CompareField(value = "包装价")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount packagingPrice;
    /**
     * 运费
     */
    @CompareField(value = "运费")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount shippingPrice;
    /**
     * 采购单价
     */
    @CompareField(value = "采购单价")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount unitPrice;
    /**
     * 总价
     */
    @CompareField(value = "总价")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount totalPrice;
    /**
     * 含税总价
     */
    @CompareField(value = "含税总价")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount withTaxPrice;
    /**
     * 税率
     */
    @CompareField(value = "税率")
    private BigDecimal taxRate;
    /**
     * 采购类型
     */
    @CompareField(value = "采购类型")
    private Integer purchaseType;
    /**
     * 内箱装量
     */
    @CompareField(value = "内箱装量")
    private Integer qtyPerInnerbox;
    /**
     * 外箱装量
     */
    @CompareField(value = "外箱装量")
    private Integer qtyPerOuterbox;
    /**
     * 包装规格长度
     */
    @CompareField(value = "包装规格长度")
    private BigDecimal packageLength;
    /**
     * 包装规格宽度
     */
    @CompareField(value = "包装规格宽度")
    private BigDecimal packageWidth;
    /**
     * 包装规格高度
     */
    @CompareField(value = "包装规格高度")
    private BigDecimal packageHeight;
    /**
     * 包装规格单位
     */
    private Integer packageUnit;
    /**
     * 单品毛重
     */
    @CompareField(value = "单品毛重")
    @TableField(typeHandler = JsonWeightTypeHandler.class)
    private JsonWeight singleGrossweight;
    /**
     * 转合同状态
     */
    @CompareField(value = "转合同状态")
    private Integer convertedFlag;
    /**
     * 备注
     */
    @CompareField(value = "备注")
    private String remark;
    /**
     * 采购员id
     */
    private Long purchaseUserId;
    /**
     * 采购员姓名
     */
    @CompareField(value = "采购员姓名")
    private String purchaseUserName;
    /**
     * 采购员部门id
     */
    private Long purchaseUserDeptId;
    /**
     * 采购员部门名称
     */
    @CompareField(value = "采购员部门名称")
    private String purchaseUserDeptName;
    /**
     * 工厂货号
     */
    @CompareField(value = "工厂货号")
    private String venderProdCode;
    /**
     * 报价日期
     */
    @CompareField(value = "报价日期")
    private LocalDateTime quoteDate;
    /**
     * 是否含运费
     */
    @CompareField(value = "是否含运费")
    private Integer freightFlag;
    /**
     * 是否含包装
     */
    @CompareField(value = "是否含包装")
    private Integer packageFlag;
    /**
     * 包装方式
     */
    @CompareField(value = "包装方式")
    private List<Long> packageType;
    /**
     * 币种
     */
    @CompareField(value = "币种")
    private String currency;
    /**
     * 是否含税
     */
    @CompareField(value = "是否含税")
    private Integer faxFlag;
    /**
     * 最小起购量
     */
    @CompareField(value = "最小起购量")
    private Integer moq;
    /**
     * 是否赠品
     */
    @CompareField(value = "是否赠品")
    private Integer freeFlag;
    /**
     * 箱数
     */
    @CompareField(value = "箱数")
    private Integer boxCount;
    /**
     * 采购模式
     */
    @CompareField(value = "采购模式")
    private Integer purchaseModel;

    /**
     * 产品类型
     */
    @CompareField(value = "产品类型")
    private Integer skuType;

    /**
     * 采购链接
     */
    @CompareField(value = "采购链接")
    private String purchaseUrl;
    /**
     * 外销合同id
     */
    private Long saleContractId;
    /**
     * 外销合同编号
     */
    @CompareField(value = "外销合同")
    private String saleContractCode;
    /**
     * 是否通用辅料
     */
    @CompareField(value = "是否通用辅料")
    private Integer auxiliarySkuFlag;
    /**
     * 辅料采购类型
     */
    @CompareField(value = "辅料采购类型")
    private Integer auxiliarySkuType;
    /**
     * 规格描述
     */
    @CompareField(value = "规格描述")
    private String specRemark;
    /**
     * 附件
     */
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    @CompareField(value = "附件")
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
     * 生成采购单时间
     */
    @CompareField(value = "生成采购单时间")
    private LocalDateTime convertTime;
    /**
     * 计量单位
     */
    @CompareField(value = "计量单位")
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
     * 销售明细主键
     */
    private Long saleContractItemId;

    /**
     * 链路编号
     */
    private List<String> linkCodeList;

    /**
     * 唯一编号
     */
    private String uniqueCode;


    /**
     * 来源编号
     */
    private String sourceUniqueCode;

    @Schema(description = "规格")
    @TableField(typeHandler = JsonSpecificationEntityListHandler.class)
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
