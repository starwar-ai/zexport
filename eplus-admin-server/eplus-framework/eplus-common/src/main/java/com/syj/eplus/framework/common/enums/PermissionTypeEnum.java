package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description：
 * @Author：du
 * @Date：2024/4/20 15:46
 */
@Getter
@AllArgsConstructor
public enum PermissionTypeEnum {
    /**
     * 多用户
     */
    FIND_IN_SET(1),
    /**
     * 单用户
     */
    SINGLE_JSON(2),

    /**
     * 多用户json
     */
    JSON_CONTAINS(3);

    private final Integer type;
}
