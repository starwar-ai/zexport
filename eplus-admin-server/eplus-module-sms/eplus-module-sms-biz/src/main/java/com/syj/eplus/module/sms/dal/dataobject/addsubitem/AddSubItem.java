package com.syj.eplus.module.sms.dal.dataobject.addsubitem;

/**
 * @Description：
 * @Author：du
 * @Date：2024/6/14 11:04
 */

import cn.iocoder.yudao.framework.common.annotations.ChangeIgnore;
import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.mybatis.core.exinterface.ChangeExInterface;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.annotations.CompareField;
import com.syj.eplus.framework.common.config.handler.DifferenceReasonListHandler;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.entity.DifferenceReason;
import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 加减项 DO
 *
 * @author ePlus
 */

@TableName(value = "sms_add_sub_term", autoResultMap = true)
@KeySequence("sms_add_sub_term") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = false)
public class AddSubItem extends BaseDO implements ChangeExInterface {

    /**
     * 主键
     */
    @TableId
    @ChangeIgnore
    private Long id;
    /**
     * 合同号
     */
    @Schema(description = "合同号")
    @ChangeIgnore
    private String contractCode;
    /**
     * 类型
     */
    @Schema(description = "类型")
    private Integer calculationType;
    /**
     * 费用名称
     */
    @Schema(description = "费用名称")
    private String feeName;

    /**
     * 金额
     */
    @Schema(description = "金额")
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount amount;

    /**
     * 变更标识
     */
    @TableField(exist = false)
    @Schema(description = "变更标识")
    private Integer changeFlag;

    /**
     * 已完成金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    @ChangeIgnore
    private JsonAmount completedAmount;

    /**
     * 状态
     */
    @CompareField(value = "状态")
    @ChangeIgnore
    private Integer status;

    /**
     * 是否转结汇
     */
    @CompareField(value = "是否转结汇")
    @ChangeIgnore
    private Integer settlementFlag;

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
}