package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/07/30/11:59
 * @Description:
 */
@Getter
@AllArgsConstructor

public enum SingleWeightUnitEnum {
    GRAM(1, "g"),
    KILOGRAM(2, "kg");

    private Integer value;

    private String name;
}
