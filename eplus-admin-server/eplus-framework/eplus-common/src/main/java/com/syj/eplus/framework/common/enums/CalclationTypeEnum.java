package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description：加减项枚举
 * @Author：du
 * @Date：2024/6/20 14:21
 */
@Getter
@AllArgsConstructor
public enum CalclationTypeEnum {
    //减项
    DEDUCTION(2, "减项"),
    //加项
    ADD(1, "加项");

    private Integer type;

    private String name;
}
