package com.syj.eplus.module.oa.api;

import com.github.xiaoymin.knife4j.core.util.StrUtil;
import com.syj.eplus.module.oa.api.dto.SimpleTravelAppRespDTO;
import com.syj.eplus.module.oa.service.travelapp.TravelAppService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class TravelAppApiImpl implements TravelAppApi {

    @Resource
    private TravelAppService travelAppService;

    @Override
    public List<SimpleTravelAppRespDTO> getSimpleTravelAppRespDTOList(List<Long> idList) {
        return travelAppService.getSimpleTravelAppRespDTOList(idList);
    }

    @Override
    public SimpleTravelAppRespDTO getSimpleTravelAppRespDTO(String reimbCode) {
        if(StrUtil.isBlank(reimbCode))
            return null;
        return travelAppService.getSimpleTravelAppRespDTO(reimbCode);
    }
}
