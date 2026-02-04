package com.syj.eplus.module.dtms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/27/18:01
 * @Description: 评价结果枚举
 */
@Getter
@AllArgsConstructor
public enum EvaluateResultEnum {

    COMPLAINT(1, "投诉"),
    EXCELLENT(2, "优秀"),
    THUMB_UP(3, "点赞");

    private final Integer value;

    private final String desc;

    public static String getDescByValue(Integer value) {
        for (EvaluateResultEnum evaluateResultEnum : EvaluateResultEnum.values()) {
            if (evaluateResultEnum.getValue().equals(value)) {
                return evaluateResultEnum.getDesc();
            }
        }
        return null;
    }
}
