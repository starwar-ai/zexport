package com.syj.eplus.module.fms.listener.receipt;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import com.syj.eplus.module.fms.service.receipt.ReceiptService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class ReceiptAuditResultListener extends BpmProcessInstanceResultEventListener {
@Resource
private ReceiptService receiptService;
@Override
protected String getProcessDefinitionKey() {
return receiptService.getProcessDefinitionKey();
}

@Override
protected void onEvent(BpmProcessInstanceResultEvent event) {
receiptService.updateAuditStatus(Long.parseLong(event.getBusinessKey()), event.getResult());
    receiptService.updateApprovalMessage(Long.parseLong(event.getBusinessKey()), event.getId());
}
}
