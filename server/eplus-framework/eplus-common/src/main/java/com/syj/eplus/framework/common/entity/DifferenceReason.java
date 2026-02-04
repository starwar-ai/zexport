package com.syj.eplus.framework.common.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/24 11:21
 */
@Data
public class DifferenceReason {
    /**
     * 差异类型
     */
    private Integer differenceType;
    /**
     * 差异金额
     */
    private BigDecimal differenceAmount;
    /**
     * 备注
     */
    private String remark;
}
