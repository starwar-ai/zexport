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
public enum PurchaseRegistrationStatusEnum {
    WAITING_FOR_APPROVAL(1, "未审批"),
    APPROVALED(2, "已审批"),
    CASE_CLOSED(3, "已作废");

    private Integer value;

    private String name;

    public static PurchaseRegistrationStatusEnum getByValue(int value) {
        for (PurchaseRegistrationStatusEnum status : PurchaseRegistrationStatusEnum.values()) {
            if (status.value == value) {
                return status;
            }
        }
        return null; // 或者抛出异常
    }
}
