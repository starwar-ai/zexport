package com.syj.eplus.framework.common.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SimpleShipment {
    /**
     * 报关中文品名
     */
    private String declarationName;

    /**
     * 报关英文品名
     */
    private String customsDeclarationNameEng;

    /**
     * 海关计量单位
     */
    private String hsMeasureUnit;

    /**
     * 退税率
     */
    private BigDecimal taxRefundRate;

    /**
     * HS编码
     */
    private String hsCode;
}
