package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/07/30/16:39
 * @Description:
 */
@Getter
@AllArgsConstructor
public enum RegisteredStatusEnum {
    PARTP_REGISTERED(2, "部分登记"),
    REGISTERED(1, "已登记"),
    NOT_REGISTERED(0, "未登记");

    private Integer value;
    private String name;
}
