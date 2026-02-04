package com.syj.eplus.module.oa.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description：
 * @Author：chengbo
 * @Date：2024/1/11 14:37
 */
@Getter
@AllArgsConstructor
public enum PrintFlagEnum {
    /**
     * 未打印
     */
    NO(0),
    /**
     * 已打印
     */
    YES(1);


    private final Integer Value;

}
