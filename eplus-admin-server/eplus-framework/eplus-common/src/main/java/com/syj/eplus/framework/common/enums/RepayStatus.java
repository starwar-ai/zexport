package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RepayStatus {
    /**
     * 未还款
     */
    NOT_REPAID(0),
    /**
     * 已还款
     */
    REPAID(1),
    /**
     * 已取消
     */
    CANCEL(2);

    private final Integer status;
}
