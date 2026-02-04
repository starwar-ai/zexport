package com.syj.eplus.module.oa.api;

import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.oa.api.dto.RepayAppDTO;

public interface RepayAppApi {
    void createRepayApp(RepayAppDTO repayAppDTO);

    void updateRepayStatus(String code);

    /**
     * 根据申请单号获取申请人信息
     * @param code 申请单号
     * @return 申请人信息
     */
    UserDept getApplyerByCode(String code);

}
