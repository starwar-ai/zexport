package com.syj.eplus.framework.common.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ConvertNoticeFlagEnum {
    NOT_ISSUED(1, "未转"),
    PART_ISSUED(2, "部分转"),
    ISSUED(3, "已转");
    private Integer status;

    private String name;

    public static String getNameByCode(Integer code) {
        for (ConvertNoticeFlagEnum convertNoticeFlagEnum : ConvertNoticeFlagEnum.values()) {
            if (convertNoticeFlagEnum.getStatus().equals(code)) {
                return convertNoticeFlagEnum.getName();
            }
        }
        return null;
    }
}
