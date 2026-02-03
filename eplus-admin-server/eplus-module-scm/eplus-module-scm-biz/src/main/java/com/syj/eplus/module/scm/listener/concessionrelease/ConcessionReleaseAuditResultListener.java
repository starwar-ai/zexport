package com.syj.eplus.module.scm.listener.concessionrelease;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import com.syj.eplus.module.scm.service.concessionrelease.ConcessionReleaseService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class ConcessionReleaseAuditResultListener extends BpmProcessInstanceResultEventListener {
    @Resource
    private ConcessionReleaseService concessionReleaseService;
    @Override
    protected String getProcessDefinitionKey() {
        return concessionReleaseService.getProcessDefinitionKey();
    }

    @Override
    protected void onEvent(BpmProcessInstanceResultEvent event) {
        concessionReleaseService.updateAuditStatus(Long.parseLong(event.getBusinessKey()), event.getResult(),null);
    }
}
