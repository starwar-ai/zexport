package com.syj.eplus.module.dtms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *  任务状态：1：待提交，2：待审批，3：待完成，4：待评价，5：已完成，6：已作废，7：已驳回
 */
@Getter
@AllArgsConstructor
public enum DesignStatusEnum {

    UN_SUBMIT(1,"待提交"),
    IN_AUDIT(2,"待审批"),
    IN_PROCESS(3,"待完成"),
    TO_BE_ASSESS(4,"待评价"),
    COMPLETED(5,"已完成"),
    CANCELED(6,"已作废"),
    REJECTED(7,"已驳回");

    private final Integer value;

    private final String desc;
}
