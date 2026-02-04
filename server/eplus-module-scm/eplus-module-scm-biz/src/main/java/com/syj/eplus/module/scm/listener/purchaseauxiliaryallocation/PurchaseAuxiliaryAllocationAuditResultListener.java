package com.syj.eplus.module.scm.listener.purchaseauxiliaryallocation;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import com.syj.eplus.module.scm.service.purchaseauxiliaryallocation.PurchaseAuxiliaryAllocationService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class PurchaseAuxiliaryAllocationAuditResultListener extends BpmProcessInstanceResultEventListener {
@Resource
private PurchaseAuxiliaryAllocationService purchaseAuxiliaryAllocationService;
@Override
protected String getProcessDefinitionKey() {
return purchaseAuxiliaryAllocationService.getProcessDefinitionKey();
}

@Override
protected void onEvent(BpmProcessInstanceResultEvent event) {
purchaseAuxiliaryAllocationService.updateAuditStatus(Long.parseLong(event.getBusinessKey()), event.getResult());
}
}
