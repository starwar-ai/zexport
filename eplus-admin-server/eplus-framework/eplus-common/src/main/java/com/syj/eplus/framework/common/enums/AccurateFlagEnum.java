package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum AccurateFlagEnum {
    //是
    YES(1,"AccurateFlag=1"),
    //否
    NO(0,"AccurateFlag=0");

    private Integer value;

    private String name;
}
