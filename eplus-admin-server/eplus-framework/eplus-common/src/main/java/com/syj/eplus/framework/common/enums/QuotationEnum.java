package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/16 15:14
 */
@Getter
@AllArgsConstructor
public enum QuotationEnum {
    PENDING_SUBMIT(1, "待提交"),

    PENDING_APPROVAL(2, "待审核"),

    APPROVED(3, "已审核"),

    REJECTED(4, "已驳回"),

    CASE_CLOSED(5, "已结案");

    private Integer code;

    private String name;
}
