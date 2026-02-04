package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description：是否变更枚举类
 * @Author：chengbo
 * @Date：2024/8/29 14:25
 */
@Getter
@AllArgsConstructor
public enum ChangeFlagEnum {
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
