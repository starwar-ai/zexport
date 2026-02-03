package com.syj.eplus.module.fms.api.payment.api.receipt.dto;

import com.sun.xml.bind.v2.TODO;
import com.syj.eplus.framework.common.entity.JsonAmount;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @Description：
 * @Author：du
 * @Date：2024/4/19 16:20
 */
@Data
@Accessors(chain = true)
public class ReceiptCreateDTO {
    /**
     * 申请单号
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
    private JsonAmount amount;
    /**
     * 实时汇率
     */
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
     * 收款方式
     */
    private Integer receiptType;

    /**
     * 流程发起人id
     */
    private Long startUserId;
}
