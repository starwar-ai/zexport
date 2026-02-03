package com.syj.eplus.module.sms.dal.dataobject.collectionplan;

/**
 * @Description：
 * @Author：du
 * @Date：2024/6/14 11:23
 */

import cn.iocoder.yudao.framework.common.annotations.ChangeIgnore;
import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.mybatis.core.exinterface.ChangeExInterface;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.config.handler.DifferenceReasonListHandler;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonCollectionPlanItemListHandler;
import com.syj.eplus.framework.common.entity.CollectionPlanItem;
import com.syj.eplus.framework.common.entity.DifferenceReason;
import com.syj.eplus.framework.common.entity.JsonAmount;
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

@TableName(value = "sms_collection_plan", autoResultMap = true)
@KeySequence("sms_collection_plan") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = false)
public class CollectionPlan extends BaseDO implements ChangeExInterface {

    /**
     * 主键
     */
    @TableId
    @ChangeIgnore
    private Long id;
    /**
     * 合同id
     */
    @ChangeIgnore
    private Long contractId;

    /**
     * 步骤
     */
    @ChangeIgnore
    private Integer step;

    /**
     * 支付方式
     */
    @ChangeIgnore
    private Integer paymentMethod;
    /**
     * 起始点
     */
    @ChangeIgnore
    private Integer dateType;

    /**
     * 起始日
     */
    @ChangeIgnore
    private LocalDateTime startDate;

    /**
     * 天数
     */
    private Integer days;

    /**
     * 预计收款日
     */
    @ChangeIgnore
    private LocalDateTime expectedReceiptDate;

    /**
     * 收款比例
     */
    @ChangeIgnore
    private BigDecimal collectionRatio;

    /**
     * 应收金额
     */
    @ChangeIgnore
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount receivableAmount;

    /**
     * 实收金额
     */
    @ChangeIgnore
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount receivedAmount;

    /**
     * 是否控制采购
     */
    @ChangeIgnore
    private Integer controlPurchaseFlag;
    /**
     * 是否控制采购
     */
    @ChangeIgnore
    private Integer controlShipmentFlag;
    /**
     * 状态
     */
    @ChangeIgnore
    private Integer exeStatus;

    /**
     * 变更标识
     */
    @ChangeIgnore
    @TableField(exist = false)
    private Integer changeFlag;

    /**
     * 差异原因
     */
    @ChangeIgnore
    @TableField(typeHandler = DifferenceReasonListHandler.class)
    private List<DifferenceReason> differenceReason;

    @TableField(exist = false)
    @ChangeIgnore
    private String sourceCode;

    @TableField(exist = false)
    @ChangeIgnore
    private List<JsonEffectRange> effectRangeList;

    @TableField(typeHandler = JsonCollectionPlanItemListHandler.class)
    @ChangeIgnore
    private List<CollectionPlanItem>  children;

    /**
     * 收款比例
     */
    private BigDecimal realCollectionRatio;
}