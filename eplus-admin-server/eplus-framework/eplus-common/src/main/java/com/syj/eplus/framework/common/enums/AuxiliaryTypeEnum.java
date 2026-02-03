package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description：
 * @Author：du
 * @Date：2024/4/24 14:06
 */
@Getter
@AllArgsConstructor
public enum AuxiliaryTypeEnum {
    CUST(1, "客户"),
    //否
    VENDER(2, "供应商");

    private Integer value;

    private String name;
}
