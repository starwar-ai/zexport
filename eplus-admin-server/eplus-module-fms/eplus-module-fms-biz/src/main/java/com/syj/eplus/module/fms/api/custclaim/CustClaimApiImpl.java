package com.syj.eplus.module.fms.api.custclaim;

import cn.hutool.core.collection.CollUtil;
import com.syj.eplus.module.fms.api.payment.api.custclaim.CustClaimApi;
import com.syj.eplus.module.fms.api.payment.api.custclaim.dto.CustClaimDTO;
import com.syj.eplus.module.fms.service.custclaim.CustClaimService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CustClaimApiImpl implements CustClaimApi {
    @Resource
    private CustClaimService service;

    @Override
    public Map<Long,List<CustClaimDTO>> getListByPlanIds(List<Long> planIds) {
        List<CustClaimDTO> listByPlanIds = service.getListByPlanIds(planIds);
       if(CollUtil.isEmpty(listByPlanIds)){
           return null;
       }
       return  listByPlanIds.stream().collect(Collectors.groupingBy(CustClaimDTO::getItemId));
    }
}
