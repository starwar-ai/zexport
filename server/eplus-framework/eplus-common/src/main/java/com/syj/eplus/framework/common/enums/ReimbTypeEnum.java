package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description：
 * @Author：du
 * @Date：2024/4/25 11:02
 */
@Getter
@AllArgsConstructor
public enum ReimbTypeEnum {
    TRAVEL(1,"出差报销"),

    GENERAL(2,"通用费用报销"),

    ENTERTAIN(3,"招待费报销"),
    OTHER(4,"其他费用报销");

    private Integer value;

    private String name;
}
