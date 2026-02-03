package com.syj.eplus.module.infra.enums.company;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 企业性质
 */
@Getter
@AllArgsConstructor
public enum OfficialSealTypeEnum {


    COMPANY_STAMP(1,"公章"),

    FINANCE_STAMP(2,"财务章"),

    LEGAL_STAMP(3,"法人章"),

    BUSINESS_STAMP(4,"业务蓝章"),

    CONTRACT_STAMP(5,"合同章"),
    ;

    private final Integer value;

    private final String desc;

}
