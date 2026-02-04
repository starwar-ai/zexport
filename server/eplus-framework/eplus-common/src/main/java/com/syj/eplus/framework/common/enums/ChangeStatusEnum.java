package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/09/02/11:30
 * @Description:
 */
@Getter
@AllArgsConstructor
public enum ChangeStatusEnum {
    NOT_CHANGED(1, "未变更"),
    IN_CHANGE(2, "变更中"),
    CHANGED(3, "已变更");
    private final Integer status;

    private final String name;
}
