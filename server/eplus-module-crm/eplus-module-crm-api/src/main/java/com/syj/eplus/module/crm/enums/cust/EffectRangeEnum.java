package com.syj.eplus.module.crm.enums.cust;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description：影响范围枚举类
 * @Author：chengbo
 * @Date：2024/8/29 14:25
 */
@Getter
@AllArgsConstructor
public enum EffectRangeEnum {
    /**
     * 销售 SMS
     */
    SMS(1),
    /**
     * 采购 SCM
     */
    SCM(2),
    /**
     * 出运 DMS
     */
    DMS(3),
    /**
     * 客户 CUST
     */
    CUST(4),
    /**
     * 供应商 VENDER
     */
    VENDER(5),
    /**
     * 产品 SKU
     */
    SKU(6),
    /**
     * 包材采购
     */
    AUXILIARY_PURCHASE(7);
    /**
     * 枚举值
     */
    private final Integer value;
}
