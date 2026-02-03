package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 验货单状态:1：待审批，2：待确认，3：待验货，4：验货不通过，5：已完成（完全验货已通过或让步放行）6：部分通过， 7：已驳回， 8：已结案
 */
@Getter
@AllArgsConstructor
public enum InspectionBillStatusEnum {
    AWAITING_APPROVAL(1, "待审批"),
    WAITING_FOR_CONFIRMATION(2, "待确认"),
    EXPECTING_INSPECTION(3, "待验货"),
    INSPECTION_FAIL(4, "验货不通过"),
    INSPECTION_PASS(5, "已完成"),
    INSPECTION_PART(6, "部分通过"),
    REJECTED(7, "已驳回"),
    CASE_SETTLED(8, "已结案");

    private final Integer value;

    private final String desc;


}
