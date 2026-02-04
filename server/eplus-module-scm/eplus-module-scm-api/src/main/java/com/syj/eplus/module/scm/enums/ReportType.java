package com.syj.eplus.module.scm.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Desc——模板类型
 * Create by Rangers at  2024-04-12 10:31
 */
@Getter
@AllArgsConstructor
public enum ReportType {

    /**
     * 基础模板
     */
    CUST(1,"基础模板"),

    /**
     * 外部模板
     */
    VENDER(2,"外部模板");


    private final Integer key;


    private final String label;
}
