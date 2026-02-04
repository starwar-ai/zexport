package com.syj.eplus.module.oa.dal.dataobject.reimbrepaydetail;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sun.xml.bind.v2.TODO;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 报销还款详情 DO
 *
 * @author ePlus
 */

@TableName(value = "oa_reimb_repay_detail",autoResultMap = true)
@KeySequence("oa_reimb_repay_detail_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReimbRepayDetailDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 报销单id
     */
    private Long reimbId;
    /**
     * 借款单id
     */
    private Long loanId;
    /**
     * 还款状态
     * <p>
     * 枚举 {@link TODO bpm_process_instance_result 对应的类}
     */
    private Integer repayStatus;
    /**
     * 还款金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount repayAmount;

    /**
     * 审核状态
     *
     * 枚举 {@link TODO bpm_process_instance_result 对应的类}
     */
    private Integer auditStatus;

    /**
     * 还款时间
     */
    private LocalDateTime repayTime;

    /**
     * 还款来源类型
     */
    private Integer repaySourceType;

    /**
     * 还款单号
     */
    private String repaySourceCode;

    /**
     * 还款人
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept repayUser;
}