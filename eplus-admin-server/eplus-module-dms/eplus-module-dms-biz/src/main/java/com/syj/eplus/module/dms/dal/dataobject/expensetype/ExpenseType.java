package com.syj.eplus.module.dms.dal.dataobject.expensetype;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * 费用类型 DO
 *
 * @author du
 */

@TableName("dms_expense_type")
@KeySequence("dms_expense_type_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseType extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 出运明细编号
     */
    private String shipmentCode;
    /**
     * 付款次数
     */
    private Integer paymentCount;
    /**
     * 船代公司编号
     */
    private String forwarderCompanyCode;
    /**
     * 船代公司名称
     */
    private String forwarderCompanyName;
    /**
     * 费用类型
     */
    private Integer expenseType;
    /**
     * 费用金额
     */
    private BigDecimal amount;
    /**
     * 币别
     */
    private String currency;
    /**
     * 已报销金额
     */
    private BigDecimal reimbursedAmount;

}