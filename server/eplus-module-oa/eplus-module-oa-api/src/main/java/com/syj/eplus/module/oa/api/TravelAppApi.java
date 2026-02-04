package com.syj.eplus.module.oa.api;

import com.syj.eplus.module.oa.api.dto.SimpleTravelAppRespDTO;

import java.util.List;

public interface TravelAppApi {

    List<SimpleTravelAppRespDTO> getSimpleTravelAppRespDTOList(List<Long> idList);

    SimpleTravelAppRespDTO getSimpleTravelAppRespDTO(String reimbCode);
}
