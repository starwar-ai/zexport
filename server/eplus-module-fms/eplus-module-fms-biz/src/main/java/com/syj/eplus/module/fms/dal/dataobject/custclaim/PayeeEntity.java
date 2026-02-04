package com.syj.eplus.module.fms.dal.dataobject.custclaim;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptListTypeHandler;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description：收款对象信息
 * @Author：du
 * @Date：2024/7/23 15:33
 */
@TableName(value = "fms_payee", autoResultMap = true)
@KeySequence("fms_payee_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayeeEntity extends BaseDO {
    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 收款对象类型
     */
    private Integer payeeType;

    /**
     * 编号
     */
    private String payeeCode;

    /**
     * 收款对象主键
     */
    @TableField(exist = false)
    private Long payeeId;

    /**
     * 名称
     */
    private String payeeName;

    /**
     * 负责员工
     */
    @TableField(typeHandler = JsonUserDeptListTypeHandler.class)
    private List<UserDept> manager;

    /**
     * 认领总金额
     */
    private BigDecimal claimTotalAmount;

    /**
     * 币种
     */
    private String currency;


    /**
     * 登记主键
     */
    private Long registrationId;

    /**
     * 新增标识
     */
    @TableField(exist = false)
    private Integer createFlag;
}
