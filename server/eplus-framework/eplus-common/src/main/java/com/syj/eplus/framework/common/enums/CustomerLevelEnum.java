package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description：
 * @Author：du
 * @Date：2024/4/24 14:13
 */
@Getter
@AllArgsConstructor
public enum CustomerLevelEnum {
    GENERAL(1,"普通"),
    //否
    IMPORTANT(2,"重要"),

    SPECIAL(2,"特级");

    private Integer level;

    private String name;
}