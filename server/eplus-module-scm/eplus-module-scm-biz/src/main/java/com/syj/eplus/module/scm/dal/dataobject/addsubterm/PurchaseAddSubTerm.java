package com.syj.eplus.module.scm.dal.dataobject.addsubterm;

import cn.iocoder.yudao.framework.common.pojo.JsonEffectRange;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.mybatis.core.exinterface.ChangeExInterface;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 采购合同加减项 DO
 *
 * @author du
 */

@TableName(value = "scm_add_sub_term",autoResultMap = true)
@KeySequence("scm_add_sub_term_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = false)
public class PurchaseAddSubTerm extends BaseDO implements ChangeExInterface {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 合同编号
     */
    private String contractCode;
    /**
     * 加/减项
     */
    private Integer calculationType;
    /**
     * 费用名称
     */
    private String feeName;
    /**
     * 金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount amount;

    @TableField(exist = false)
    private Integer changeFlag;

    @TableField(exist = false)
    private String sourceCode;

    @TableField(exist = false)
    private List<JsonEffectRange> effectRangeList;

    private String linkContractCode;
}