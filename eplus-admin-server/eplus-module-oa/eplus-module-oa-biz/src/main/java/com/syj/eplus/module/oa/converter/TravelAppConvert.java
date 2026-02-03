package com.syj.eplus.module.oa.converter;


import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.oa.api.dto.SimpleTravelAppRespDTO;
import com.syj.eplus.module.oa.dal.dataobject.travelapp.TravelAppDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface TravelAppConvert {

    TravelAppConvert INSTANCE = Mappers.getMapper(TravelAppConvert.class);

    SimpleTravelAppRespDTO convertTravelDTOFromTravelDO(TravelAppDO travelAppDO);

    List<SimpleTravelAppRespDTO> convertTravelRespDTOList(List<TravelAppDO> travelAppDOList);

    default PageResult<SimpleTravelAppRespDTO> convertTravelPageResult(PageResult<TravelAppDO> travelAppDOPageResult) {
        Long total = travelAppDOPageResult.getTotal();
        List<TravelAppDO> travelAppDOList = travelAppDOPageResult.getList();
        if (CollUtil.isEmpty(travelAppDOList)) {
            return PageResult.empty(total);
        }
        List<SimpleTravelAppRespDTO> simpleTravelAppRespDTOList = convertTravelRespDTOList(travelAppDOList);
        return new PageResult<SimpleTravelAppRespDTO>().setTotal(total).setList(simpleTravelAppRespDTOList);
    }
}
