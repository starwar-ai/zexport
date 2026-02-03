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
public enum ToShiomentFormTypeEnum {

    /**
     * 按单据转
     */
    BYDOCUMENT("1"),

    /**
     * 按客户转
     */
    BYCUST("2");

    private final String value;

}
