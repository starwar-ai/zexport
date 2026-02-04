package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description：
 * @Author：du
 * @Date：2024/4/20 18:21
 */
@Getter
@AllArgsConstructor
public enum ReceiptStatusEnum {
    RECEIVED(1,"已收款"),
    AMOUNT_DUE(0,"未收款");

    private Integer value;

    private String name;
}
