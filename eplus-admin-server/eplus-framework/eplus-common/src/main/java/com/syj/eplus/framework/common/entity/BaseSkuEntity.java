package com.syj.eplus.framework.common.entity;

import lombok.Data;

import java.util.List;

@Data
public class BaseSkuEntity {
    /**
     * 含税单价
     */
    private JsonAmount withTaxPrice;
    /**
     * 装箱量
     */
    private Integer qtyPerOuterbox;

    /**
     * 规格
     */
    private List<JsonSpecificationEntity> specificationList;

    /**
     * 数量
     */
    private Integer quantity;

}
