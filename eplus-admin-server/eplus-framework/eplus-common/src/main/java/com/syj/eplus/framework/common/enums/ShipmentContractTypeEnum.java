package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 出运单合同类型枚举
 *
 * @author 波波
 */
@Getter
@AllArgsConstructor
public enum ShipmentContractTypeEnum {

    /**
     * 本次采购
     */
    CURRENT_PURCHASE("本次采购", 1),

    /**
     * 库存采购
     */
    STOCK_PURCHASE("库存采购", 2);

    /**
     * 类型名称
     */
    private final String name;

    /**
     * 类型值
     */
    private final Integer value;

    /**
     * 根据值获取名称
     *
     * @param value 类型值
     * @return 类型名称
     */
    public static String getNameByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (ShipmentContractTypeEnum typeEnum : values()) {
            if (typeEnum.getValue().equals(value)) {
                return typeEnum.getName();
            }
        }
        return null;
    }

    /**
     * 根据名称获取值
     *
     * @param name 类型名称
     * @return 类型值
     */
    public static Integer getValueByName(String name) {
        if (name == null) {
            return null;
        }
        for (ShipmentContractTypeEnum typeEnum : values()) {
            if (typeEnum.getName().equals(name)) {
                return typeEnum.getValue();
            }
        }
        return null;
    }

    /**
     * 根据名称获取枚举
     *
     * @param name 类型名称
     * @return 枚举对象
     */
    public static ShipmentContractTypeEnum getByName(String name) {
        if (name == null) {
            return null;
        }
        for (ShipmentContractTypeEnum typeEnum : values()) {
            if (typeEnum.getName().equals(name)) {
                return typeEnum;
            }
        }
        return null;
    }
}
