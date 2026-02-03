package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum SaleTabEnum {
    SALE_ONLY(0, "基本数据"),
    SALE_DETAIL(1, "订单详情"),
    SALE_FEE(2, "订单费用");
    private final Integer status;

    private final String name;
}
