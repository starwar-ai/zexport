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
public enum PayeeTypeEnum {
    CUST(1,"客户"),
    VENDER(2,"供应商"),
    EMPLOYEE(3,"员工");

    private final Integer type;

    private final String name;
}
