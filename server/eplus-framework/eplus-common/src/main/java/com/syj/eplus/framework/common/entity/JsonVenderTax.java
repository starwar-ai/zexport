package com.syj.eplus.framework.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsonVenderTax {
    /**
     * 发票类型
     */
    private Integer taxType;
    /**
     * 税率
     */
    private BigDecimal taxRate;
    /**
     * 币种
     */
    private String currency;
    /**
     * 是否默认
     */
    private Integer defaultFlag;
}
