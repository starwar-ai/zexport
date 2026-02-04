package com.syj.eplus.module.oa.controller.admin.reimb.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class ReimbStandardRespVO {
    /**
     * 补贴类型
     */
    private String subsidyType;
    /**
     * 补贴金额
     */
    private BigDecimal amount;
}
