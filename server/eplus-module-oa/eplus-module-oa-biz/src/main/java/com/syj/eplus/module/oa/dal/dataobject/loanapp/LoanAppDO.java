package com.syj.eplus.module.oa.dal.dataobject.loanapp;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sun.xml.bind.v2.TODO;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonFileListTypeHandler;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 借款申请单 DO
 *
 * @author ePlus
 */
@TableName(value = "oa_loan_app", autoResultMap = true)
@KeySequence("oa_loan_app_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanAppDO extends BaseDO {

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * 单据编号
     */
    private String code;
    /**
     * 打印状态
     *
     * 枚举 {@link TODO print_flag 对应的类}
     */
    private Integer printFlag;
    /**
     * 打印次数
     */
    private Integer printTimes;
    /**
     * 审核状态
     *
     * 枚举 {@link TODO bpm_process_instance_result 对应的类}
     */
    private Integer auditStatus;
    /**
     * 借款事由
     */
    private String purpose;
    /**
     * 申请人
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept applyer;
    /**
     * 借款金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount amount;
    /**
     * 借款申请日期
     */
    private LocalDateTime loanDate;
    /**
     * 借款方式
     *
     * 枚举 {@link TODO loan_type 对应的类}
     */
    private Integer loanType;
    /**
     * 开户行
     */
    private String bank;
    /**
     * 银行地址
     */
    private String bankAddress;
    /**
     * 银行账号
     */
    private String bankAccount;
    /**
     * 开户行联系人
     */
    private String bankPoc;
    /**
     * 银行行号
     */
    private String bankCode;
    /**
     * 支付状态
     *
     * 枚举 {@link TODO loan_payment_status 对应的类}
     */
    private Integer paymentStatus;
    /**
     * 已转金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount paymentAmount;
    /**
     * 支付日期
     */
    private LocalDateTime paymentDate;
    /**
     * 还款状态
     *
     * 枚举 {@link TODO loan_repay_status 对应的类}
     */
    private Integer repayStatus;
    /**
     * 已还金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount repayAmount;
    /**
     * 实际还款日期
     */
    private LocalDateTime repayDate;
    /**
     * 剩余未还款金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount outstandingAmount;
    /**
     * 内部法人单位
     */
    private Long companyId;

    /**
     * 出纳员
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept cashier;
    /**
     * 借款类型
     */
    private Integer loanSource;
    /**
     * 附件
     */
    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> annex;
    /**
     * 借款状态
     */
    private Integer loanStatus;
    /**l
     * 截止时间
     */
    private LocalDateTime deadlineTime;
    /**
     * 采购合同编号
     */
    private String purchaseContractCode;
    /**
     * 供应商id
     */
    private Long venderId;
    /**
     * 供应商编号
     */
    private String venderCode;
    /**
     * 供应商名称
     */
    private String venderName;
    /**
     * 汇率
     */
    private String exchangeRate;
}