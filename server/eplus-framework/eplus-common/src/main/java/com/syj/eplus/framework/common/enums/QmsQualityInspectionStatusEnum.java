package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum QmsQualityInspectionStatusEnum {
    PENDING_APPROVAL(1, "待审批"),
    PENDING_CONFIRMATION(2, "待确认"),
    PENDING_INSPECTION(3, "待验货"),
    INSPECTION_FAILED(4, "验货不通过"),
    INSPECTION_PASSED_WITH_RELEASE(5, "验货通过（含放行）"),
    PART_PASSED(6, "部分通过"),
    REJECTED(7, "已驳回"),
    CANCELLED(8, "已作废");
    private final Integer status;

    private final String name;
}
