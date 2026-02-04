package com.syj.eplus.module.infra.api.countryinfo.dto;

import lombok.Data;

/**
 * 国家信息 DTO
 *
 * @author du
 * @date 2024/3/20 10:47
 */
@Data
public class CountryInfoDTO {

    /**
     * 国家id
     */
    private Long id;

    /**
     * 国家名称
     */
    private String name;

    /**
     * 国家编码
     */
    private String code;

    /**
     * 区域编码
     */
    private String regionCode;

    /**
     * 区域名称
     */
    private String regionName;

    /**
     * 电话区号
     */
    private String areaCode;
}
