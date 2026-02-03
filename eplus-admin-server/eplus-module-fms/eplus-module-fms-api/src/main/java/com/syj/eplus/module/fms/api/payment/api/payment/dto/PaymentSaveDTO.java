package com.syj.eplus.module.fms.api.payment.api.payment.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.syj.eplus.framework.common.config.handler.JsonUserDeptTypeHandler;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.PaymentApplyEntity;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Accessors(chain = true)
public class PaymentSaveDTO {
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
     */    private String bankAddress;
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
     */
    private Integer status = 0;
    /**
     * 支付金额
     */
    private JsonAmount amount;
    /**
     * 支付日期
     */
    private LocalDateTime date;
    /**
     * 出纳员编号
     */
    private Long cashierId;
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
    private List<JsonAmount> applyAmount;

    /**
     * 申请单信息
     */
    private PaymentApplyEntity paymentApply;

    /**
     * 支付方式
     */
    private Integer paymentMethod;

    /**
     * 申请付款日
     */
    private LocalDateTime applyPaymentDate;

    /**
     * 申请单编号
     */
    private String applyCode;

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
    private List<String> linkCodeList;

    /**
     * 承兑天数
     */
    private Integer acceptanceDays;

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
