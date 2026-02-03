package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PurchaseModelEnum {
    STANDARD(1, "标准采购"),
    COMBINE(2, "组套件采购");


    private final Integer code;

    private final String name;


}
