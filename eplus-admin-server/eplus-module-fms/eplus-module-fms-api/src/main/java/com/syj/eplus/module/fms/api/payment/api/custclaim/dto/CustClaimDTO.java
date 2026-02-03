package com.syj.eplus.module.fms.api.payment.api.custclaim.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.syj.eplus.framework.common.entity.DifferenceReason;
import com.syj.eplus.framework.common.entity.UserDept;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Accessors(chain = true)
public class CustClaimDTO {
    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 订单合同号
     */
    private String contractCode;

    /**
     * 客户编号
     */
    private String custCode;

    /**
     * 客户名称
     */
    private String custName;

    /**
     * 订单币别
     */
    private String currency;

    /**
     * 收款方式主键
     */
    private Long settlementId;

    /**
     * 收款方式名称
     */
    private String settlementName;

    /**
     * 来源
     */
    private Integer sourceType;

    /**
     * 应收金额
     */
    private BigDecimal receivableAmount;

    /**
     * 已收金额
     */
    private BigDecimal receivedAmount;

    /**
     * 本次入账币种认领金额
     */
    private BigDecimal claimedAmount;

    /**
     * 订单币种认领金额
     */
    private BigDecimal contractAmount;

    /**
     * 差异总金额
     */
    private BigDecimal differenceAmount;

    /**
     * 收款完成标识
     */
    private Integer completedFlag;

    /**
     * 认领员工
     */
    private UserDept claimPerson;

    /**
     * 认领日期
     */
    private LocalDateTime claimDate;

    /**
     * 差异原因
     */
    private List<DifferenceReason> differenceReason;

    /**
     * 登记主键
     */
    private Long registrationId;

    /**
     * 明细主键
     */
    private Long itemId;

    private String source;

    /**
     * 认领类型 0:回款认领 1：其他收费
     */
    private Integer type;

    /**
     * 其他收费类型  1:证书费  2:模具费  3:样品费  4:快递费 5:验货费
     */
    private Integer otherFeeType;

    private UserDept registeredBy;
    private LocalDateTime receiveDate;

    /**
     * 收款单编号
     */
    private String receiptCode;
}
