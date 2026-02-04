package com.syj.eplus.module.dms.dal.dataobject.temporarysku;

import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.mybatis.core.exinterface.ChangeExInterface;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonSpecificationEntityListHandler;
import com.syj.eplus.framework.common.config.handler.JsonWeightTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.JsonWeight;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 出运单明细 DO
 *
 * @author du
 */

@TableName(value = "dms_temporary_sku", autoResultMap = true)
@KeySequence("dms_temporary_sku_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "临时产品")
@Accessors(chain = false)
public class TemporarySku extends BaseDO implements ChangeExInterface {

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 出运单主键
     */
    private Long shipmentId;

    /**
     * 客户
     */
    @Schema(description = "客户")
    @CompareField(value = "客户")
    private String custName;

    /**
     * 海关编码
     */
    @Schema(description = "海关编码")
    @CompareField(value = "海关编码")
    private String hsCode;

    /**
     * 报关要素
     */
    @Schema(description = "报关要素")
    @CompareField(value = "报关要素")
    private String declarationElement;

    /**
     * 报关品名
     */
    @Schema(description = "报关品名")
    @CompareField(value = "报关品名")
    private String declarationName;

    /**
     * 报关英文名
     */
    @Schema(description = "报关英文名")
    @CompareField(value = "报关英文名")
    private String declarationNameEng;

    /**
     * 出货数量
     */
    @Schema(description = "出货数量")
    @CompareField(value = "出货数量")
    private Integer expectCount;

    /**
     * 海关计量单位
     */
    @Schema(description = "海关计量单位")
    @CompareField(value = "海关计量单位")
    private String hsMeasureUnit;

    /**
     * 报关数量
     */
    @Schema(description = "报关数量")
    @CompareField(value = "报关数量")
    private Integer declarationCount;

    /**
     * 箱数
     */
    @Schema(description = "箱数")
    @CompareField(value = "箱数")
    private Integer boxCount;

    /**
     * 计价方式
     */
    @Schema(description = "计价方式")
    @CompareField(value = "计价方式")
    private Integer pricingMethod;

    /**
     * 报关单位
     */
    @Schema(description = "报关单位")
    @CompareField(value = "报关单位")
    private String declarationUnit;

    /**
     * 报关总价
     */
    @Schema(description = "报关总价")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    @CompareField(value = "报关总价")
    private JsonAmount declarationTotalPrice;

    /**
     * 总体积
     */
    @Schema(description = "总体积")
    @CompareField(value = "总体积")
    private String totalVolume;

    @Schema(description = "总净重")
    @TableField(typeHandler = JsonWeightTypeHandler.class)
    @CompareField(value = "总净重")
    private JsonWeight totalNetweight;

    @Schema(description = "总毛重")
    @TableField(typeHandler = JsonWeightTypeHandler.class)
    @CompareField(value = "总毛重")
    private JsonWeight totalGrossweight;

    @TableField(exist = false)
    private Integer changeFlag;

    @TableField(exist = false)
    private String sourceCode;

    @TableField(exist = false)
    private List<JsonEffectRange> effectRangeList;

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
}