package com.syj.eplus.module.sms.dal.dataobject.quotationitem;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonFileTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonSpecificationEntityListHandler;
import com.syj.eplus.framework.common.config.handler.LongListTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.SimpleFile;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 报价单子 DO
 *
 * @author ePlus
 */

@TableName(value = "sms_quotation_item", autoResultMap = true)
@KeySequence("sms_quotation_item_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotationItemDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 报价单id
     */
    private Long smsQuotationId;

    /**
     * 产品编码
     */
    @TableField(exist = false)
    private Long skuId;
    /**
     * 产品编码
     */
    private String skuCode;

    /**
     * 客户货号
     */
    private String cskuCode;

    /**
     * 客户货号
     */
    private String basicSkuCode;

    /**
     * 图片
     */
    @TableField(typeHandler = JsonFileTypeHandler.class)
    private SimpleFile mainPicture;
    /**
     * 中文名称
     */
    private String name;
    /**
     * 英文名称
     */
    private String nameEng;
    /**
     * 报价
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount quotation;
    /**
     * 数量
     */
    private Integer moq;
    /**
     * 供应商id
     */
    private Long venderId;
    /**
     * 供应商编号
     */
    private String venderCode;
    /**
     * 供应商名称
     */
    private String venderName;
    /**
     * 工厂报价
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount withTaxPrice;
    /**
     * 产品规格
     */
    private String spec;
    /**
     * 包装方式
     */
    @TableField(typeHandler = LongListTypeHandler.class)
    private List<Long> packageType;
    /**
     * 内箱装量
     */
    private Integer qtyPerInnerbox;
    /**
     * 外箱装量
     */
    private Integer qtyPerOuterbox;
    /**
     * 箱数
     */
    private Integer boxCount;
    /**
     * 外箱单位
     */
    private Integer unitPerOuterbox;
    /**
     * 中文说明
     */
    private String description;
    /**
     * 英文说明
     */
    private String descriptionEng;
    /**
     * HS编码
     */
    private String hsCode;
    /**
     * 交货日期
     */
    private LocalDateTime quoteDate;

    /**
     * 利润率
     */
    @CompareField(value = "利润率")
    private BigDecimal profitRate;

    /**
     * 20尺柜
     */
    private Integer twentyFootCabinetNum;

    /**
     * 40尺柜
     */
    private Integer fortyFootCabinetNum;

    /**
     * 40尺高柜
     */
    private Integer fortyFootContainerNum;

    /**
     * 散货
     */
    private BigDecimal bulkHandlingVolume;


    /**
     * 包装方式名称
     */
    @TableField(exist = false)
    private String packageTypeName;
    /**
     * 客户产品标记
     */
    private Integer custProFlag;
    /**
     * 自营产品标记
     */
    private Integer ownBrandFlag;

    /**
     * 规格
     */
    @TableField(typeHandler = JsonSpecificationEntityListHandler.class)
    private List<JsonSpecificationEntity> specificationList;

    /**
     * 是否分箱
     */
    private Integer splitBoxFlag;
}