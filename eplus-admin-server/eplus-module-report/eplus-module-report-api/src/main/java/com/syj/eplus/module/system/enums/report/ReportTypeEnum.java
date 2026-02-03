package com.syj.eplus.module.system.enums.report;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description：模板类型：1：基础模板，2：外部模板，3：可选模板
 * @Author：chengbo
 * @Date：2024/1/11 14:33
 */
@Getter
@AllArgsConstructor
public enum ReportTypeEnum {
    /**
     * 基础模板
     */
    BASE(1),
    /**
     * 账套模版
     */
    COMPANY(2),
    /**
     * 外部模板
     */
    CUSTOM(3),
    /**
     * 可选模板
     */
    SPECIFIC(4);
    private final Integer value;
}
