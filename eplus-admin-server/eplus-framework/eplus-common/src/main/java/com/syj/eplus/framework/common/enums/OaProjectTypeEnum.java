package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum OaProjectTypeEnum {
    DEV(1,"研发项目"),
    EXMS(2,"展会项目"),
    BRAND(3,"品牌项目");
    private final Integer Code;

    private final String name;
}
