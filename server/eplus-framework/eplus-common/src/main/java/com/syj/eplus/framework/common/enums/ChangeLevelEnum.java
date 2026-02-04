package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description：
 * @Author：du
 * @Date：2024/3/11 11:05
 */
@Getter
@AllArgsConstructor
public enum ChangeLevelEnum {

    /**
     * 普通级别
     */
    NORMAL(1),
    /**
     * 表单级别
     */
    FORM(2);

    private final Integer value;

}
