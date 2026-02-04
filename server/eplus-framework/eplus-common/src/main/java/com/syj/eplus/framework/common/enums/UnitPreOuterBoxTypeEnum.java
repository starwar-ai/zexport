package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum UnitPreOuterBoxTypeEnum {
    //箱
    CTNS(1,"CTNS"),
    //托盘
    PKGS(2,"PKGS"),

    PLTS(3,"PLTS"),

    BAGS(4,"BAGS");

    private Integer value;

    private String name;

    public static String getNameByValue(Integer value) {
        for (UnitPreOuterBoxTypeEnum unitPreOuterBoxTypeEnum : UnitPreOuterBoxTypeEnum.values()) {
            if (unitPreOuterBoxTypeEnum.getValue().equals(value)) {
                return unitPreOuterBoxTypeEnum.getName();
            }
        }
        return null;
    }
    public static Integer getValueByName(String name) {
        for (UnitPreOuterBoxTypeEnum unitPreOuterBoxTypeEnum : UnitPreOuterBoxTypeEnum.values()) {
            if (unitPreOuterBoxTypeEnum.getName().equals(name)) {
                return unitPreOuterBoxTypeEnum.getValue();
            }
        }
        return null;
    }
}
