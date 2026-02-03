package com.syj.eplus.module.fms.api.payment.api.payment.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.syj.eplus.framework.common.config.handler.*;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.PaymentApplyEntity;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Accessors(chain = true)
public class PaymentDTO {
    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 支付编码
     */
    private String code;
    /**
     * 内部法人单位
     */
    private Long companyId;
    /**
     * 开户行
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
     * 支付状态
     */
    private Integer status;
    /**
     * 支付金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount amount;
    /**
     * 支付日期
     */
    private LocalDateTime date;
    /**
     * 出纳员
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept cashier;
    /**
     * 备注
     */
    private String remark;
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
     * 申请人
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept applyer;
    /**
     * 打印状态
     */
    private Integer printFlag;
    /**
     * 打印次数
     */
    private Integer printTimes;
    /**
     * 审核状态
     */
    private Integer auditStatus;

    /**
     * 申请支付金额
     */
    @TableField(typeHandler = JsonAmountListTypeHandler.class)
    private List<JsonAmount> applyAmount;
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
     * 开户行联系人
     */
    private String bankPoc;

    /**
     * 银行行号
     */
    private String bankCode;

    /**
     * 申请单编号
     */
    private String applyCode;

    /**
     * 申请付款日期
     */
    private LocalDateTime applyPaymentDate;

    /**
     * 支付方式
     */
    private Integer paymentMethod;

    /**
     * 付款申请信息
     */
    @TableField(typeHandler = JsonPaymentApplyEntityHandler.class)
    private PaymentApplyEntity paymentApply;

    /**
     * 对方银行
     */
    private String targetBank;

    /**
     * 对方账户
     */
    private String targetBankAccount;

    /**
     * 对方账号
     */
    private String targetBankPoc;

    /**
     * 链路编号列表
     */
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> linkCodeList;

    /**
     * 已付金额
     */
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount paidAmount;

    /**
     * 作废时间
     */
    private LocalDateTime cancelTime;

    /**
     * 作废原因
     */
    private String cancelReason;

    /**
     * 作废人
     */
    @TableField(typeHandler = JsonUserDeptTypeHandler.class)
    private UserDept cancelUser;

}
