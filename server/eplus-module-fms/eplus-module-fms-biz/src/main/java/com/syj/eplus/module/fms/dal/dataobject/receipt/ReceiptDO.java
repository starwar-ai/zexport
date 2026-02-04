package com.syj.eplus.module.fms.dal.dataobject.receipt;

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
 * 财务收款单 DO
 *
 * @author ePlus
 */

@TableName(value = "fms_receipt", autoResultMap = true)
@KeySequence("fms_receipt_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 申请单号
     */
    private String code;
    /**
     * 打印状态
     * <p>
     * 枚举 {@link TODO print_flag 对应的类}
     */
    private Integer printFlag;
    /**
     * 打印次数
     */
    private Integer printTimes;
    /**
     * 审核状态
     * <p>
     * 枚举 {@link TODO bpm_process_instance_result 对应的类}
     */
    private Integer auditStatus;
    /**
     * 内部法人单位编号
     */
    private Long companyId;
    /**
     * 开户行
     */
    private String bank;
    /**
     * 开户行地址
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
     * 收款金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount amount;
    /**
     * 实时汇率
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount rate;
    /**
     * 收款时间
     */
    private LocalDateTime receiptTime;
    /**
     * 收款备注
     */
    private String remark;
    /**
     * 收款人
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept receiptUser;
    /**
     * 最终审批人
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept approver;
    /**
     * 审批时间
     */
    private LocalDateTime approvalTime;

    /**
     * 业务类型
     */
    private Integer businessType;

    /**
     * 业务编号
     */
    private String businessCode;

    /**
     * 支付对象类型
     */
    private Integer businessSubjectType;
    /**
     * 支付对象编号
     */
    private String businessSubjectCode;

    /**
     * 收款状态
     */
    private Integer status;

    /**
     * 收款方式
     */
    private Integer receiptType;

    /**
     * 回款认领ID
     */
    private Integer custClaimId;
}