package com.syj.eplus.module.pms.controller.admin.sku.vo;

import lombok.Data;

/**
 * @Description：
 * @Author：du
 * @Date：2024/5/7 16:07
 */
@Data
public class SimpleSkuSaveReqVO {
    /**
     * sku id
     */
    private Long id;

    /**
     * sku code
     */
    private String skuCode;
    /**
     * sku code
     */
    private String code;
    /**
     * sku 数量
     */
    private Integer qty;
    private Integer auxiliarySkuFlag;

    /**
     * 自营产品标识
     */
    private  Integer ownBrandFlag;
}
