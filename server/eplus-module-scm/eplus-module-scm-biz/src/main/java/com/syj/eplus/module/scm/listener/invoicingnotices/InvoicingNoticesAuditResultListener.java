package com.syj.eplus.module.scm.listener.invoicingnotices;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import com.syj.eplus.module.scm.service.invoicingnotices.InvoicingNoticesService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class InvoicingNoticesAuditResultListener extends BpmProcessInstanceResultEventListener {
    @Resource
    private InvoicingNoticesService invoicingNoticesService;

    @Override
    protected String getProcessDefinitionKey() {
        return invoicingNoticesService.getProcessDefinitionKey();
    }

    @Override
    protected void onEvent(BpmProcessInstanceResultEvent event) {
        invoicingNoticesService.updateAuditStatus(Long.parseLong(event.getBusinessKey()), event.getResult(), null);
    }
}
