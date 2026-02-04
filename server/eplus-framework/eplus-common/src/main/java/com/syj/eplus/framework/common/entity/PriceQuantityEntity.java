package com.syj.eplus.framework.common.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PriceQuantityEntity {

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 价格
     */
    private JsonAmount withTaxPrice;

    /**
     * 税率
     */
    private BigDecimal taxRate;

    /**
     * 价格(不包含赠品)
     */
    private JsonAmount withTaxPriceRemoveFree;
}
