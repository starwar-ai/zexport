package com.syj.eplus.module.oa.dal.dataobject.expenseapp;


import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptListTypeHandler;
import com.syj.eplus.framework.common.config.handler.StringListTypeHandler;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.oa.entity.JsonExpenseAppItem;
import com.syj.eplus.module.oa.handler.JsonExpenseAppItemListHandler;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@TableName(value = "oa_reception_expense_app", autoResultMap = true)
@KeySequence("oa_reception_expense_app_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseApp extends BaseDO {
    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 编号
     */
    private String spNo;

    /**
     * 申请人
     */
    private Long applyerId;

    /**
     * 日期
     */
    private LocalDateTime expenseDate;

    /**
     * 客户名称
     */
    private String custName;

    /**
     * 我司陪同人员
     */
    @TableField(typeHandler = JsonUserDeptListTypeHandler.class)
    private List<UserDept> companions;

    /**
     * 招待人数
     */
    private Integer entertainNum;

    /**
     * 费用归属订单
     */
    private String orderNo;

    /**
     * 客户等级
     */
    private String custLevel;

    /**
     * 本次招待成效
     */
    private String entertainEffect;

    /**
     * 关联申请单编号
     */
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> relateAppCode;

    /**
     * 明细
     */
    @TableField(typeHandler = JsonExpenseAppItemListHandler.class)
    private List<JsonExpenseAppItem> children;
}
