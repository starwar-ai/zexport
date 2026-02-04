package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExmsExpoStatusEnum {
    PENDING_SUBMIT(1, "待提交"),
    PENDING_APPROVE(2, "待审核"),
    PROJECT (3, "已立项"),
    DONE(4, "已完成"),
    FINISH(5, "已结案"),
    REJECT(6,"已驳回");

    private final Integer code;

    private final String name;


}
