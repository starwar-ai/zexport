package com.syj.eplus.module.exms.api.exhibition.dto;

import lombok.Data;

@Data
public class ExhibitionDTO {

    /**
     * 展会id
     */
    private Long exhibitionId;

    /**
     * 展会系列id
     */
    private Long exmsEventCategoryId;

    /**
     * 展会名称
     */
    private String exhibitionName;

    /**
     * 展会系列名称
     */
    private String exmsEventCategoryName;
}
