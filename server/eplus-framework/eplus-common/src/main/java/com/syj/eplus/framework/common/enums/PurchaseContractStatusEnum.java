package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PurchaseContractStatusEnum {
    READY_TO_SUBMIT(1, "待提交"),
    AWAITING_APPROVAL(2, "待审批"),
    REJECTED(3, "已驳回"),
    AWAITING_ORDER(4, "待下单"),
    EXPECTING_DELIVERY(5, "待到货"),
    FINISHED(6, "已完成"),
    CASE_SETTLED(7, "已结案");

    private final Integer code;

    private final String name;

    public static PurchaseContractStatusEnum getByValue(int value) {
        for (PurchaseContractStatusEnum status : PurchaseContractStatusEnum.values()) {
            if (status.code == value) {
                return status;
            }
        }
        return null; // 或者抛出异常
    }

}
