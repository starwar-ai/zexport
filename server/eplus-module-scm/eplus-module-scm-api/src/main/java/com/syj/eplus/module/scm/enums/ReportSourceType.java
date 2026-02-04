package com.syj.eplus.module.scm.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Desc——外部模板类型
 * Create by Rangers at  2024-04-12 10:31
 */
@Getter
@AllArgsConstructor
public enum ReportSourceType {

    /**
     * 客户
     */
    CUST(1,"客户"),

    /**
     * 供应商
     */
    VENDER(2,"供应商");


    private final Integer key;


    private final String label;
}
