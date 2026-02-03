package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description：
 * @Author：du
 * @Date：2024/4/20 20:49
 */
@Getter
@AllArgsConstructor
public enum ReceiptBusinessType {

    /**
     * 还款
     */
    REPAY(1,"还款");

    private final Integer value;
    private final String name;

}
