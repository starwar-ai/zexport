package com.syj.eplus.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description：
 * @Author：du
 * @Date：2024/1/10 10:34
 */

@Getter
@AllArgsConstructor
public enum FileBusinessTypeEnum {
    /**
     * 客户附件
     */
    CUSTOM_ANNEX(1),
    /**
     * 客户图片
     */
    CUSTOM_PICTURE(2),
    /**
     * 供应商附件
     */
    VENDER_ANNEX(3),

    /**
     * SPU图片
     */
    SPU_PICTURE(4),
    /**
     * SPU附件
     */
    SPU_AMMEX(5),

    /**
     * 报销发票
     */
    TRAVELREIMB_INVOICE(6),

    /**
     * 出差申请附件
     */
    TRAVELAPP_ANNEX(7),

    /**
     * 客户联系人名片
     */
    CUST_POC_CORD(8),

    /**
     * 潜在客户附件
     */
    CLUE_ANNEX(9),

    /**
     * 潜在客户图片
     */
    CLUE_PICTURE(10),

    /**
     * 采购计划附件
     */
    PURCHASE_PLAN_ANNEX(11);

    private final Integer type;
}
