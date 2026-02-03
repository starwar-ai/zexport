package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description：
 * @Author：du
 * @Date：2024/3/20 13:51
 */

@Getter
@AllArgsConstructor
public enum BooleanEnum {
    //是
    YES(1,"是"),
    //否
    NO(0,"否");

    private Integer value;

    private String name;
}
