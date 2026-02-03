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
public enum SaleContractStatusEnum {
    WAITING_FOR_SUBMISSION(1, "待提交"),

    WAITING_FOR_APPROVAL(2, "待审核"),

    WAITING_FOR_COUNTERSIGNATURE(3, "待回签"),

    REJECTED(4, "已驳回"),

    WAITING_FOR_PROCUREMENT(5, "待采购"),

    WAITING_FOR_SHIPMENT(6, "待出运"),

    COMPLETED(7, "已完成"),

    CASE_CLOSED(8, "已作废");

    private Integer value;

    private String name;

    public static SaleContractStatusEnum getByValue(int value) {
        for (SaleContractStatusEnum status : SaleContractStatusEnum.values()) {
            if (status.value == value) {
                return status;
            }
        }
        return null; // 或者抛出异常
    }
}
