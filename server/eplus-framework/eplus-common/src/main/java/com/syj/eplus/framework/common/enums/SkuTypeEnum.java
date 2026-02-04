package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description：
 * @Author：du
 * @Date：2024/5/8 15:49
 */
@Getter
@AllArgsConstructor
public enum SkuTypeEnum {
    // 普通产品
    GENERAL_PRODUCTS(1, "普通产品"),
    // 组合产品
    PRODUCT_MIX(2, "组合产品"),
    // 配件
    ACCESSORIES(3, "配件"),
    // 辅料
    AUXILIARY_MATERIALS(4, "辅料");

    private Integer value;

    private String name;
}
