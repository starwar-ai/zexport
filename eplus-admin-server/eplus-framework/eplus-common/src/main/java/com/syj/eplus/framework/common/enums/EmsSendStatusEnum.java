package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmsSendStatusEnum {
    PENDING_SUBMIT(1, "待提交"),
    PENDING_APPROVE(2, "待审批"),
    REJECT(3, "已驳回"),
    PENDING_SEND(4, "待寄出"),
    ALREADY_SEND(5, "已寄出"),
    PENGDING_PAY(6, "待请款"),
    COMPLETED(7, "已完成"),
    CLOSE(8, "已作废");

    private final Integer code;

    private final String name;


}
