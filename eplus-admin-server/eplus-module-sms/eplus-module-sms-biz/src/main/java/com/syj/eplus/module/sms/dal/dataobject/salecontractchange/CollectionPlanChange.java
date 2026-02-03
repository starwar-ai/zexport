package com.syj.eplus.module.sms.dal.dataobject.salecontractchange;

import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import cn.iocoder.yudao.framework.mybatis.core.exinterface.ChangeExInterface;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/2 17:37
 */
@Data
@Accessors(chain = false)
@Schema(description = "收款计划")
public class CollectionPlanChange implements ChangeExInterface {
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
     * 步骤
     */
    private Integer step;

    /**
     * 起始点
     */
    private Integer dateType;

    /**
     * 起始日
     */
    private LocalDateTime startDate;

    /**
     * 天数
     */
    private Integer days;

    /**
     * 预计收款日
     */
    private LocalDateTime expectedReceiptDate;

    /**
     * 收款比例
     */
    private BigDecimal collectionRatio;

    /**
     * 应收金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount receivableAmount;

    /**
     * 实收金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount receivedAmount;

    /**
     * 是否控制采购
     */
    private Integer controlPurchaseFlag;

    /**
     * 状态
     */
    private Integer exeStatus;

    /**
     * 变更标识
     */
    @TableField(exist = false)
    private Integer changeFlag;

    /**
     * 步骤(旧)
     */
    private Integer old_step;

    /**
     * 起始点(旧)
     */
    private Integer old_dateType;

    /**
     * 起始日(旧)
     */
    private LocalDateTime old_startDate;

    /**
     * 天数(旧)
     */
    private Integer old_days;

    /**
     * 预计收款日(旧)
     */
    private LocalDateTime old_expectedReceiptDate;

    /**
     * 收款比例(旧)
     */
    private BigDecimal old_collectionRatio;

    /**
     * 应收金额(旧)
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount old_receivableAmount;

    /**
     * 实收金额(旧)
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount old_receivedAmount;

    /**
     * 是否控制采购(旧)
     */
    private Integer old_controlPurchaseFlag;

    @TableField(exist = false)
    private String sourceCode;

    @TableField(exist = false)
    private List<JsonEffectRange> effectRangeList;
}
