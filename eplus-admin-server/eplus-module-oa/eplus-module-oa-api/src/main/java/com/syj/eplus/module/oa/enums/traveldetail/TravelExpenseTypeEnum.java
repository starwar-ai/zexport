package com.syj.eplus.module.oa.enums.traveldetail;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description：出差费用小项枚举
 * @Author：du
 * @Date：2024/3/22 14:02
 */
@Getter
@AllArgsConstructor
public enum TravelExpenseTypeEnum {
    // 出差补贴
    TRAVEL_ALLOWANCE(1, "出差补贴"),
    // 住宿费
    ACCOMMODATION_FEE(2, "住宿费"),
    // 自驾
    SELF_DRIVING(3, "自驾"),
    // 交通费
    TRAFFIC_FEE(4, "交通费"),
    OTHER_AMOUNT(5, "其他");

    private final int code;
    private final String description;


    // 可选：根据code获取枚举值
    public static TravelExpenseTypeEnum fromCode(int code) {
        for (TravelExpenseTypeEnum type : values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid expense type code: " + code);
    }
}
