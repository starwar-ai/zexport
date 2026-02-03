package com.syj.eplus.module.qms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 验货状态:1:待验货 2：成功，3：失败，4：待定
 */
@Getter
@AllArgsConstructor
public enum InspectionItemStatusEnum {

    PENDING(1,"待定"),
    SUCESS(3,"成功"),
    FAIL(2,"失败"),
    ;

    private final Integer value;

    private final String desc;

    public static String getDescByValue(Integer value) {
        for (InspectionItemStatusEnum enumValue : InspectionItemStatusEnum.values()) {
            if (enumValue.getValue().equals(value)) {
                return enumValue.getDesc();
            }
        }
        return null;
    }

    public static List<Integer> getValues() {
        List<Integer> values = new ArrayList<>();
        for (InspectionItemStatusEnum enumValue : InspectionItemStatusEnum.values()) {
            values.add(enumValue.getValue());
        }
        return values;
    }

}
