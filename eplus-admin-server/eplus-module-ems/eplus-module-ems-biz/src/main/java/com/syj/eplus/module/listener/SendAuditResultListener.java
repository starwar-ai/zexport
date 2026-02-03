package com.syj.eplus.module.listener;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import com.syj.eplus.module.service.send.SendService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class SendAuditResultListener extends BpmProcessInstanceResultEventListener {

    @Resource
    private SendService sendService;
    @Override
    protected String getProcessDefinitionKey() {
        return sendService.getProcessDefinitionKey();
    }

    @Override
    protected void onEvent(BpmProcessInstanceResultEvent event) {

        long auditableId = Long.parseLong(event.getBusinessKey());
        Integer result = event.getResult();
        sendService.updateAuditStatus(auditableId, result);

    }
}
