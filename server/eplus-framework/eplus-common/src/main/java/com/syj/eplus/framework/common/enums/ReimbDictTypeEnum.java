package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 报销字典类型枚举
 * 
 * @Author：波波
 * @Date：2024/12/19
 */
@Getter
@AllArgsConstructor
public enum ReimbDictTypeEnum {
    
    /**
     * 一般费用报销
     */
    GENERAL_EXPENSE(1, "一般费用报销"),
    
    /**
     * 招待费报销
     */
    ENTERTAINMENT_EXPENSE(2, "招待费报销"),
    
    /**
     * 差旅费
     */
    TRAVEL_EXPENSE(3, "差旅费");

    /**
     * 枚举值
     */
    private Integer value;
    
    /**
     * 枚举名称
     */
    private String name;
} 