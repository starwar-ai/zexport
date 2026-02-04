package com.syj.eplus.module.crm.enums.cust;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description：客户类型枚举类
 * @Author：du
 * @Date：2024/1/11 14:25
 */
@Getter
@AllArgsConstructor
public enum CustomTypeEnum {
    /**
     * 电商 E-commerce
     */
    E_COMMERCE(1),
    /**
     * 进口商 importer
     */
    IMPORTER(2),
    /**
     * 零售商 retailer
     */
    RETAILER(3),
    /**
     * 批发商 wholesaler
     */
    WHOLESALER(4),
    /**
     * 邮购商 Mail orderers
     */
    MAIL_ORDERERS(5);
    /**
     * 枚举值
     */
    private final Integer value;
}
