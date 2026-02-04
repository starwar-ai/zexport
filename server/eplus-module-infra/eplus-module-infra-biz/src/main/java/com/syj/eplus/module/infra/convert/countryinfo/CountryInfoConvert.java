package com.syj.eplus.module.infra.convert.countryinfo;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import com.syj.eplus.module.infra.api.countryinfo.dto.CountryInfoDTO;
import com.syj.eplus.module.infra.dal.dataobject.countryinfo.CountryInfoDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;

/**
 * 国家信息 Convert
 *
 * @author du
 */
@Mapper
public interface CountryInfoConvert {
    CountryInfoConvert INSTANCE = Mappers.getMapper(CountryInfoConvert.class);

    CountryInfoDTO convertDTO(CountryInfoDO countryInfoDO);

    default List<CountryInfoDTO> convertDTOList(List<CountryInfoDO> countryInfoDOList) {
        if (CollUtil.isEmpty(countryInfoDOList)) {
            return Collections.emptyList();
        }
        return CollectionUtils.convertList(countryInfoDOList, CountryInfoConvert.INSTANCE::convertDTO);
    }
}
