package com.syj.eplus.module.ems.api.send;

import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.module.ems.api.send.dto.EmsSendDetailDTO;
import com.syj.eplus.module.ems.api.send.dto.EmsSendUpdateDTO;

import java.util.List;

public interface EmsSendApi {
    void updateSendStatusByCodeList(EmsSendUpdateDTO emsSendUpdateDTO) throws Exception;

    void updatePayStatusByCodeList(EmsSendUpdateDTO emsSendUpdateDTO);

    /**
     * 更新寄件归属状态
     * @param id 主键
     * @param status 状态
     */
    void updateBelongFlagById(Long id,Integer status);


    JsonAmount getFeeShareAmountByCode(String businessCode);

    void updateBelongFlagByCode(String businessKey, Integer value);

    List<EmsSendDetailDTO> getDetailListByCodes(List<String> relationCode);
}
