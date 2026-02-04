package com.syj.eplus.module.oa.dal.dataobject.feeshareitem;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import lombok.*;

/**
 * 费用归集明细 DO
 *
 * @author zhangcm
 */

@TableName(value = "oa_fee_share_item",autoResultMap = true)
@KeySequence("oa_fee_share_item_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeShareItemDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 费用归属id
     */
    private Long shareFeeId;
    /**
     * 费用归属对象类型
     */
    private Integer businessSubjectType;
    /**
     * 费用归属对象编号
     */
    private String businessSubjectCode;
    /**
     * 金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount amount;
    /**
     * 费用标签
     */
    private Integer descId;

}