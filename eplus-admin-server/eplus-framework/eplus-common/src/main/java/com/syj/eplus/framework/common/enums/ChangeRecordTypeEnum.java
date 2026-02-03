package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description：
 * @Author：du
 * @Date：2024/5/25 14:43
 */

@Getter
@AllArgsConstructor
public enum ChangeRecordTypeEnum {
    GENERAL_CHANGE(1, "普通变更"),
    AUDIT_CHANGE(2, "审核变更");
    private final Integer type;

    private final String name;
}
