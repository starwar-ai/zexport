package com.syj.eplus.module.fms.dal.dataobject.bankregistration;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sun.xml.bind.v2.TODO;
import com.syj.eplus.framework.common.config.handler.JsonSimpleDataListTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptListTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptTypeHandler;
import com.syj.eplus.framework.common.entity.SimpleData;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 银行登记 DO
 *
 * @author du
 */

@TableName(value = "fms_bank_registration", autoResultMap = true)
@KeySequence("fms_bank_registration_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankRegistrationDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 编号
     */
    private String code;
    /**
     * 登记人
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept registeredBy;
    /**
     * 登记日期
     */
    private LocalDateTime registrationDate;
    /**
     * 入账单位id
     */
    private Long companyId;
    /**
     * 入账单位名称
     */
    private String companyName;
    /**
     * 公司抬头
     */
    private String companyTitle;
    /**
     * 银行入账日期
     */
    private LocalDateTime bankPostingDate;
    /**
     * 银行
     */
    private String bank;
    /**
     * 银行账号
     */
    private String bankAccount;
    /**
     * 开户行地址
     */
    private String bankAddress;
    /**
     * 开户行联系人
     */
    private String bankPoc;
    /**
     * 银行行号
     */
    private String bankCode;
    /**
     * 入账币别
     */
    private String currency;
    /**
     * 入账金额
     */
    private BigDecimal amount;
    /**
     * 备注
     */
    private String remark;
    /**
     * 客户编号
     */
    private String custCode;
    /**
     * 客户名称
     */
    private String custName;
    /**
     * 业务员
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept manager;
    /**
     * 认领业务员
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept claimManager;
    /**
     * 认领状态
     *
     * 枚举 {@link TODO is_int 对应的类}
     */
    private Integer claimStatus;
    /**
     * 认领日期
     */
    private LocalDateTime claimDate;
    /**
     * 关联外销合同号
     */
    private String linkSaleContractCode;

    /**
     * 已认领金额
     */
    private BigDecimal claimedAmount;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 业务员列表
     */
    @TableField(typeHandler = JsonUserDeptListTypeHandler.class)
    private List<UserDept> managerList;

    /**
     * 客户列表
     */
    @TableField(typeHandler = JsonSimpleDataListTypeHandler.class)
    private List<SimpleData> custList;

}