package com.syj.eplus.module.api;


import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.module.ems.api.send.EmsSendApi;
import com.syj.eplus.module.ems.api.send.dto.EmsSendDetailDTO;
import com.syj.eplus.module.ems.api.send.dto.EmsSendUpdateDTO;
import com.syj.eplus.module.service.send.SendService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EmsSendApiImpl implements EmsSendApi {

    @Resource
    private SendService sendService;
    @Override
    public void updateSendStatusByCodeList(EmsSendUpdateDTO emsSendUpdateDTO) throws Exception {
        sendService.updateSendStatusByCodeList(emsSendUpdateDTO);
    }

    @Override
    public void updatePayStatusByCodeList(EmsSendUpdateDTO emsSendUpdateDTO) {
        sendService.updatePayStatusByCodeList(emsSendUpdateDTO);
    }

    @Override
    public void updateBelongFlagById(Long id, Integer status) {
        sendService.updateBelongFlagById(id,status);
    }

    @Override
    public JsonAmount getFeeShareAmountByCode(String businessCode) {
        return sendService.getFeeShareAmountByCode(businessCode);
    }

    @Override
    public void updateBelongFlagByCode(String businessKey, Integer status) {
        sendService.updateBelongFlagByCode(businessKey,status);
    }

    @Override
    public List<EmsSendDetailDTO> getDetailListByCodes(List<String> relationCode) {
        return sendService.getDetailListByCodes(relationCode);
    }

}
