package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VenderLoanStatusEnum {
    PRE_SUBMIT(1, "待提交"),
    PRE_APPROVE(2, "待审批"),
    APPROVE(3, "已审批"),
    REJECT(4, "已驳回"),
    FINISH(5, "已结案");

    private Integer value;

    private String name;
}
