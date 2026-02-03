package com.syj.eplus.module.fms.service.custclaim;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.fms.api.payment.api.custclaim.dto.CustClaimDTO;
import com.syj.eplus.module.fms.controller.admin.bankregistration.vo.SimpleRegistrationResp;
import com.syj.eplus.module.fms.controller.admin.custclaim.vo.CustClaimPageReq;
import com.syj.eplus.module.fms.controller.admin.custclaim.vo.CustClaimSaveReqVO;
import com.syj.eplus.module.fms.dal.dataobject.custclaim.CustClaimItem;

import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/23 15:32
 */
public interface CustClaimService {
    PageResult<SimpleRegistrationResp> getCustClaimList(CustClaimPageReq custClaimPageReq);

    SimpleRegistrationResp getCustClaim(Long id);

    void createCustClaim(CustClaimSaveReqVO custClaimSaveReqVO);

    SimpleRegistrationResp getCustClaimDetail(Long id);

    List<CustClaimItem> getCustClaimItemList(List<String> custCodes,Long companyId);

    void cancelCustClaim(Long id);

    List<CustClaimDTO> getListByPlanIds(List<Long> planIds);
}
