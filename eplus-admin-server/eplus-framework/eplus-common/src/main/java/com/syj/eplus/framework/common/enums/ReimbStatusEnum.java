package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/20/17:59
 * @Description:
 */
@Getter
@AllArgsConstructor
public enum ReimbStatusEnum {
    REIMBURSED(1, "已使用"),
    NOT_REIMBURSED(0, "未使用");

    private Integer value;
    private String name;
}
