package com.syj.eplus.module.oa.enums.entertain;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description：招待费用小项枚举
 * @Author：du
 * @Date：2024/4/24 11:45
 */
@Getter
@AllArgsConstructor
public enum EntertainExpenseTypeEnum {
    // 餐饮费
    DINING_EXPENSE(1, "餐饮费"),
    // 礼品费
    GIFT_EXPENSE(2, "礼品费"),
    // 其他费用
    OTHER_EXPENSE(3, "其他费用");
    private final int code;
    private final String description;

    /**
     * 可选：根据code获取枚举值
     * @param code
     * @return
     */
    public static EntertainExpenseTypeEnum fromCode(int code) {
        for (EntertainExpenseTypeEnum type : values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid expense type code: " + code);
    }
}
