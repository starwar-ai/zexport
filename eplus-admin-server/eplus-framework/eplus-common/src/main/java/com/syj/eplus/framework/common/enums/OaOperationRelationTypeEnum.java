package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum OaOperationRelationTypeEnum {

    DEVELOP(1,"项目研发费用"),
    EXHIBITION(1,"展会费用"),
    BRAND(1,"品牌建设费用"),
    E_COMMERCE(2,"跨境电商费用"),
    OVERSEAS(3,"海外团队费用");
    private final Integer Code;

    private final String name;
}
