package com.syj.eplus.module.scm.dal.dataobject.quoteitem;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sun.xml.bind.v2.TODO;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonSpecificationEntityListHandler;
import com.syj.eplus.framework.common.config.handler.JsonWeightTypeHandler;
import com.syj.eplus.framework.common.config.handler.LongListTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.JsonWeight;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 供应商报价明细 DO
 *
 * @author ePlus
 */

@TableName(value = "scm_quote_item",autoResultMap = true)
@KeySequence("scm_quote_item_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuoteItemDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 供应商报价单id
     */
    private Long quoteId;
    /**
     * SKU编号
     */
    private String skuCode;
    /**
     * 供应商编号
     */
    private String venderCode;

    /**
     * 供应商主键
     */
    private Long venderId;
    /**
     * 供应商名称
     */
    private String venderName;
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
     * <p>
     * 枚举 {@link TODO confirm_type 对应的类}
     */
    private Integer freightFlag;
    /**
     * 是否含包装
     * <p>
     * 枚举 {@link TODO confirm_type 对应的类}
     */
    private Integer packageFlag;
    /**
     * 包装方式
     * <p>
     * 枚举 {@link TODO package_type 对应的类}
     */
    @TableField(typeHandler = LongListTypeHandler.class)
    private List<Long> packageType;
    /**
     * 币种
     */
    private String currency;
    /**
     * 是否含税
     * <p>
     * 枚举 {@link TODO confirm_type 对应的类}
     */
    private Integer faxFlag;
    /**
     * 税率
     */
    private BigDecimal taxRate;
    /**
     * 最小起购量
     */
    private Integer moq;
    /**
     * 包装价
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount packagingPrice;
    /**
     * 运费
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount shippingPrice;
    /**
     * 采购单价
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount unitPrice;
    /**
     * 总价
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount totalPrice;
    /**
     * 含税总价
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount withTaxPrice;
    /**
     * 交期
     */
    private Integer delivery;
    /**
     * 采购链接
     */
    private String purchaseUrl;
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
     * 包装规格宽度
     */
    private BigDecimal packageWidth;
    /**
     * 包装规格高度
     */
    private BigDecimal packageHeight;
    /**
     * 单品毛重
     */
    @TableField(typeHandler = JsonWeightTypeHandler.class)
    private JsonWeight singleGrossweight;
    /**
     * 是否默认
     */
    private Integer defaultFlag;
    /**
     * 备注
     */
    private String remark;

    /**
     * 产品主键
     */
    private Long skuId;

    /**
     * 开票品名
     */
    private String invoiceName;

    /**
     * 40尺柜装量
     */
    private Long fortyFootContainerCapacity;

    /**
     * 40尺柜装数量
     */
    private Long fortyFootContainerContainNum;

    /**
     * 40高柜装量
     */
    private Long fortyFootHighContainerCapacity;

    /**
     * 40高柜装数量
     */
    private Long fortyFootHighContainerContainNum;

    /**
     * 20尺柜装量
     */
    private Long twentyFootContainerCapacity;

    /**
     *  20尺柜装数量
     */
    private Long twentyFootContainerContainNum;

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
     * 基础产品编号
     */
    @TableField(exist = false)
    private String baseSkuCode;
}
