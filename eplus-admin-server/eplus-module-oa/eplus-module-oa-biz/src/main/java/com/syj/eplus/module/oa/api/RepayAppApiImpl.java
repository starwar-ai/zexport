package com.syj.eplus.module.oa.api;

import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.oa.api.dto.RepayAppDTO;
import com.syj.eplus.module.oa.convert.repayapp.RepayAppConvert;
import com.syj.eplus.module.oa.service.repayapp.RepayAppService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RepayAppApiImpl implements RepayAppApi {

    @Resource
    private RepayAppService repayAppService;

    @Override
    public void createRepayApp(RepayAppDTO repayAppDTO) {
        repayAppService.createRepayApp(RepayAppConvert.INSTANCE.convertRepayAppSaveReq(repayAppDTO));
    }

    @Override
    public void updateRepayStatus(String code) {
        repayAppService.updateRepayStatus(code);
    }

    @Override
    public UserDept getApplyerByCode(String code) {
        return repayAppService.getApplyerByCode(code);
    }
}
