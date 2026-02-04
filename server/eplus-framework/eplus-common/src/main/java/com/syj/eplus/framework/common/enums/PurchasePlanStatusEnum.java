package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PurchasePlanStatusEnum {
    PENDING_SUBMIT(1, "未提交"),
    PENDING_APPROVAL(2, "待审批"),
    PROCUREMENT_PENDING(3, "待采购"),
    REJECTED(4, "已驳回"),
    COMPLETED(5, "已完成"),
    CASE_CLOSED(6, "已结案");

    private final Integer code;

    private final String name;


}
