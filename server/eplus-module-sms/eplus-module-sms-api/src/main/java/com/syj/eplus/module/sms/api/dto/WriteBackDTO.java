package com.syj.eplus.module.sms.api.dto;

import com.syj.eplus.framework.common.entity.CollectionPlanItem;
import com.syj.eplus.framework.common.entity.DifferenceReason;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/26 9:41
 */
@Data
public class WriteBackDTO {
    /**
     * 认领id
     */
    private Long id;
    /**
     * 订单币种认领金额
     */
    private BigDecimal contractAmount;

    /**
     * 订单币别
     */
    private String currency;

    /**
     * 来源
     */
    private Integer sourceType;

    /**
     * 明细主键
     */
    private Long itemId;

    /**
     * 差异原因
     */
    private List<DifferenceReason> differenceReason;

    /**
     * 收款完成标识
     */
    private Integer completedFlag;

    private CollectionPlanItem collectionPlanItem;
}
