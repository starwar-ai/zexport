package com.syj.eplus.module.oa.service.travelapp.listener;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import com.syj.eplus.module.oa.service.travelapp.TravelAppService;
import com.syj.eplus.module.oa.service.travelapp.TravelAppServiceImpl;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class TravelAppResultListener extends BpmProcessInstanceResultEventListener {

    @Resource
    private TravelAppService travelAppService;

    @Override
    protected String getProcessDefinitionKey() {
        return TravelAppServiceImpl.PROCESS_DEFINITION_KEY;
    }

    @Override
    protected void onEvent(BpmProcessInstanceResultEvent event) {
        travelAppService.updateTravelAppResult(Long.parseLong(event.getBusinessKey()), event.getResult());
    }

}