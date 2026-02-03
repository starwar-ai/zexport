package com.syj.eplus.module.exms.listener.exhibition;


import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import com.syj.eplus.module.exms.service.exhibition.ExhibitionService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ExhibitionAuditResultListener extends BpmProcessInstanceResultEventListener {
    @Resource
    private ExhibitionService exhibitionService;
    @Override
    protected String getProcessDefinitionKey() {
        return exhibitionService.getProcessDefinitionKey();
    }

    @Override
    protected void onEvent(BpmProcessInstanceResultEvent event) {
        exhibitionService.updateAuditStatus(Long.parseLong(event.getBusinessKey()), event.getResult());
    }
}
