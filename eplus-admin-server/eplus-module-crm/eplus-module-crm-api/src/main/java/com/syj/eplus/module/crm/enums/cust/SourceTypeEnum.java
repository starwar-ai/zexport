package com.syj.eplus.module.crm.enums.cust;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description：
 * @Author：du
 * @Date：2024/1/11 14:37
 */
@Getter
@AllArgsConstructor
public enum SourceTypeEnum {
    /**
     * 阿里巴巴 Alibaba
     */
    ALIBABA(1,"阿里巴巴"),
    /**
     * 促销活动 Promotions
     */
    PROMOTIONS(2,"促销活动"),
    /**
     * 广交会 Fair
     */
    FAIR(3,"广交会"),
    /**
     * 国外会展 Foreign exhibitions
     */
    FOREIGN_EXHIBITIONS(4,"国外会展"),
    /**
     * 合作伙伴 Partners
     */
    PARTNERS(5,"合作伙伴"),
    /**
     * 互联网 internet
     */
    INTERNET(6,"互联网"),
    /**
     * 老客户介绍 old customers
     */
    OLD_CUSTOMERS(7,"老客户介绍");


    private final Integer Value;
    private String name;

    public static SourceTypeEnum getByValue(int value) {
        for (SourceTypeEnum status : SourceTypeEnum.values()) {
            if (status.Value == value) {
                return status;
            }
        }
        return null; // 或者抛出异常
    }

}
