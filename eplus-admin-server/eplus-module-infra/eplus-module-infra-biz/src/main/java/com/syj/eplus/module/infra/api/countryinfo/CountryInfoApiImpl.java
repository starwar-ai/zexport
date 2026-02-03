package com.syj.eplus.module.infra.api.countryinfo;

import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import com.syj.eplus.module.infra.api.countryinfo.dto.CountryInfoDTO;
import com.syj.eplus.module.infra.convert.countryinfo.CountryInfoConvert;
import com.syj.eplus.module.infra.dal.dataobject.countryinfo.CountryInfoDO;
import com.syj.eplus.module.infra.service.countryinfo.CountryInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 国家信息 API 实现类
 *
 * @author du
 * @date 2024/3/20 11:01
 */
@Service
public class CountryInfoApiImpl implements CountryInfoApi {

    @Resource
    private CountryInfoService countryInfoService;

    @Override
    public Map<Long, CountryInfoDTO> getCountryInfoMap() {
        List<CountryInfoDO> countryInfo = countryInfoService.getCountryInfo();
        List<CountryInfoDTO> countryInfoDTOList = CountryInfoConvert.INSTANCE.convertDTOList(countryInfo);
        return CollectionUtils.convertMap(countryInfoDTOList, CountryInfoDTO::getId);
    }

    @Override
    public List<CountryInfoDTO> getCountryListByRegionCode(String regionCode) {
        List<CountryInfoDO> doList = countryInfoService.getCountryListByRegionCode(regionCode);
        return CountryInfoConvert.INSTANCE.convertDTOList(doList);
    }

    @Override
    public CountryInfoDTO getCountryInfoById(Long id) {
        return countryInfoService.getCountryInfoById(id);
    }
}
