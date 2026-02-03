package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/07/30/11:26
 * @Description:
 */
@Getter
@AllArgsConstructor
public enum InvoiceStatusEnum {
    NOT_ISSUED(0, "未开票"),
    PART_ISSUED(2, "部分开票"),
    ISSUED(1, "已开票"),
    TRANSFER_ORDER(3, "转单中");

    private Integer value;

    private String name;

    public static String getNameByValue(Integer value) {
        for (InvoiceStatusEnum invoiceStatusEnum : InvoiceStatusEnum.values()) {
            if (invoiceStatusEnum.getValue().equals(value)) {
                return invoiceStatusEnum.getName();
            }
        }
        return null;
    }
}
