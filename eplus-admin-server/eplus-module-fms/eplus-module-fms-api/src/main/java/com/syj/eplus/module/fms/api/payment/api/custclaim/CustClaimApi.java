package com.syj.eplus.module.fms.api.payment.api.custclaim;

import com.syj.eplus.module.fms.api.payment.api.custclaim.dto.CustClaimDTO;

import java.util.List;
import java.util.Map;

public interface CustClaimApi {

    Map<Long,List<CustClaimDTO>> getListByPlanIds(List<Long> planIds);
}
