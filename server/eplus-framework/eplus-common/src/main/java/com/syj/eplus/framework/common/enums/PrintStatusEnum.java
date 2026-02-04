package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description：
 * @Author：du
 * @Date：2024/2/23 16:59
 */
@Getter
@AllArgsConstructor
public enum PrintStatusEnum {
    /**
     * 未打印
     */
    NOT_PRINTED(0),
    /**
     * 已打印
     */
    PRINTED(1);

    private final Integer status;
}
