package com.syj.eplus.module.crm.enums.cust;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description：是否影响主流程枚举类
 * @Author：chengbo
 * @Date：2024/8/29 14:25
 */
@Getter
@AllArgsConstructor
public enum EffectMainInstanceFlagEnum {
    /**
     * 否 no
     */
    NO(0),
    /**
     * 是 yes
     */
    YES(1);
    /**
     * 枚举值
     */
    private final Integer value;
}
