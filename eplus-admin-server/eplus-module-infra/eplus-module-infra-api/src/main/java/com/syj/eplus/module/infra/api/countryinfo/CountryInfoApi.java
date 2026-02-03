package com.syj.eplus.module.infra.api.countryinfo;

import com.syj.eplus.module.infra.api.countryinfo.dto.CountryInfoDTO;

import java.util.List;
import java.util.Map;

/**
 * 国家信息 API 接口
 *
 * @author du
 * @date 2024/3/20 10:46
 */
public interface CountryInfoApi {

    /**
     * 获取国家信息
     *
     * @return 国家信息缓存
     */
    Map<Long, CountryInfoDTO> getCountryInfoMap();

    /**
     * 根据区域编码获取国家列表
     *
     * @param regionCode 区域编码
     * @return 国家信息列表
     */
    List<CountryInfoDTO> getCountryListByRegionCode(String regionCode);

    /**
     * 通过id获取国家信息
     *
     * @param id id
     * @return 国家信息
     */
    CountryInfoDTO getCountryInfoById(Long id);
}
