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
public enum MenuTypeEnum {
    ML(1, "目录"),
    CD(2, "菜单"),
    AN(3, "按钮");
    private final Integer value;
    private final String name;
}
