package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description：
 * @Author：du
 * @Date：2024/3/11 11:05
 */
@Getter
@AllArgsConstructor
public enum CommodityInspectionTypeEnum {

    /**
     * 自主商检
     */
    OWNER(2),

    /**
     * 供应商商检
     */
    VENDER(1);

    private final Integer value;

}
