package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/16 15:45
 */
@Getter
@AllArgsConstructor
public enum ConfirmSourceTypeEnum {
    CUST(1, "客户"),
    VENDER(2, "供应商"),
    SKU(3, "产品"),
    SALE_CONTRACT(4, "销售合同"),
    PURCHASE_CONTRACT(5, "采购合同"),
    SHIPMENT(6, "出运明细");

    private Integer value;

    private String name;
}
