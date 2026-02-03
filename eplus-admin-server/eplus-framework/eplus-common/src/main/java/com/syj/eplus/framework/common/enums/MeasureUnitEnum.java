package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/07/30/13:39
 * @Description:
 */
@Getter
@AllArgsConstructor
public enum MeasureUnitEnum {
    PCS(1, "开启"),
    KG(2, "关闭"),
    SET(3, "关闭"),
    PALLET(4, "关闭");
    private final Integer value;

    private final String name;
}
