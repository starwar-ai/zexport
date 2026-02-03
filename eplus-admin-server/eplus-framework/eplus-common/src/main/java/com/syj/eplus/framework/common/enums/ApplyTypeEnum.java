package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/24 18:03
 */
@Getter
@AllArgsConstructor
public enum ApplyTypeEnum {
    TRAVEL(1,"出差申请"),

    GENERAL(2,"费用申请"),

    ENTERTAIN(3,"招待费申请"),
    OTHER(4,"其他费用申请");

    private final Integer Value;

    private final String Code;
}
