package com.syj.eplus.framework.common.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContractItemBillStatusEnum {
    NOT_STORE(1, "未入库"),
    VENDER_STORE(2, "入供应商库"),
    VERTAK_STORE(3, "入泛太库");

    private Integer status;

    private String name;
}
