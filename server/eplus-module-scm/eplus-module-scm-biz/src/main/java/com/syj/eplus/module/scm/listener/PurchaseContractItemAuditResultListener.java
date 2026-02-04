package com.syj.eplus.module.scm.listener;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import com.syj.eplus.module.scm.service.purchasecontractitem.PurchaseContractItemService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class PurchaseContractItemAuditResultListener extends BpmProcessInstanceResultEventListener {
@Resource
private PurchaseContractItemService purchaseContractItemService;
@Override
protected String getProcessDefinitionKey() {
return purchaseContractItemService.getProcessDefinitionKey();
}

@Override
protected void onEvent(BpmProcessInstanceResultEvent event) {
purchaseContractItemService.updateAuditStatus(Long.parseLong(event.getBusinessKey()), event.getResult());
}
}
