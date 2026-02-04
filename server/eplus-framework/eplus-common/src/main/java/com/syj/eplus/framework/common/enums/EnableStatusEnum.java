package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description：
 * @Author：du
 * @Date：2024/4/20 17:35
 */
@Getter
@AllArgsConstructor
public enum EnableStatusEnum {
    TURN_ON("1","开启"),
    TURN_OFF("0","关闭");
    private final String status;

    private final String name;
}
