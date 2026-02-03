package com.syj.eplus.framework.common.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CollectionPlanItem {
    /**
     * 认领id
     */
    private Long custClaimItemId;
    /**
     * 收款单号
     */
    private String settlementCode;

    /**
     * 实际收款日期
     */
    private LocalDateTime date;

    /**
     * 收款金额
     */
    private JsonAmount amount;

    /**
     * 收款人
     */
    private UserDept user;

    /**
     * 出运发票号
     */
    private String invoiceCode;
    /**
     * 是否收款完成
     */
    private Integer completedFlag;
    /**
     * 差异原因
     */
    private List<DifferenceReason> differenceReason;
}
