package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum FeeShareTypeEnum {
    CUST(1, "客户"),
    VENDER(2, "供应商"),
    OPERATE(3, "公司运营费用"),
    MARK(4, "市场拓展"),
    DEPT(5, "归属部门");

    private Integer value;

    private String name;
}
