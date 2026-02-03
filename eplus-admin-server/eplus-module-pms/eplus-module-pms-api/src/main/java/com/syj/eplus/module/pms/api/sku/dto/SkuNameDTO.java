package com.syj.eplus.module.pms.api.sku.dto;

import lombok.Data;

@Data
public class SkuNameDTO {
    /**
     * 名称
     */
    private String name;

    /**
     * 英文名称
     */
    private String nameEng;

    /**
     * 报关中文名称
     */
    private String declarationName;

    /**
     * 报关英文名称
     */
    private String customsDeclarationNameEng;
}
