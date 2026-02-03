package com.syj.eplus.module.crm.enums.cust;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description：
 * @Author：du
 * @Date：2024/1/11 14:42
 */
@Getter
@AllArgsConstructor
public enum TransportTypeEnum {
    /**
     * 供应商送货 Supplier delivery
     */
    SUPPIER_DELIVERY(1),
    /**
     * 海运 ocean shipping
     */
    OCEAN_SHIPPING(2),
    /**
     * 陆运 Land
     */
    LAND(3),
    /**
     * 客户自提 Customer pick-up
     */
    CUSTOMER_PICKUP(4),
    /**
     * 快递 express delivery
     */
    EXPRESS_DELIVERY(5),
    /**
     * 空运 airlift
     */
    AIRLIFT(6);


    private final Integer value;
}
