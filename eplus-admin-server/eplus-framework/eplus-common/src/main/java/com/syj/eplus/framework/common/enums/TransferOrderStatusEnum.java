package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/13/14:34
 * @Description:
 */
@Getter
@AllArgsConstructor
public enum TransferOrderStatusEnum {
    PENDING_SUBMIT(1, "待提交"),
    DONE(2, "已完成");

    private Integer status;

    private String name;
}
