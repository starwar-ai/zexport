package com.syj.eplus.module.qms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 验货方式: 1： 泛太陪验（工厂）
 *           2：泛太陪验（公司内）
 *           3：泛太自验（工厂）
 *           4：泛太自验（公司内）
 *           5：客户自检
 *           6：客户指定第三方
 *           7：远程验货
 */
@Getter
@AllArgsConstructor
public enum InspectionTypeEnum {

    VERTAK_ACCOMPANY_FACTORY(1,"泛太陪验（工厂）"),
    VERTAK_ACCOMPANY_COMPANY(2,"泛太陪验（公司内）"),
    VERTAK_SELF_FACTORY(3,"泛太自验（工厂）"),
    VERTAK_SELF_COMPANY(4,"泛太自验（公司内）"),

    CUST(5,"客户自检"),
    CUST_APPOINT(6,"客户指定第三方"),
    REMOTE(7,"远程验货");

    private final Integer value;

    private final String desc;

    public static List<Integer> listVertakType() {
        return Arrays.asList(VERTAK_ACCOMPANY_FACTORY.value,VERTAK_ACCOMPANY_COMPANY.value,
                VERTAK_SELF_FACTORY.value,VERTAK_SELF_COMPANY.value);
    }
}
