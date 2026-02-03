package com.syj.eplus.module.scm.listener;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import com.syj.eplus.module.scm.service.purchaseplanitem.PurchasePlanItemService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class PurchasePlanItemAuditResultListener extends BpmProcessInstanceResultEventListener {
@Resource
private PurchasePlanItemService purchasePlanItemService;
@Override
protected String getProcessDefinitionKey() {
return purchasePlanItemService.getProcessDefinitionKey();
}

@Override
protected void onEvent(BpmProcessInstanceResultEvent event) {
purchasePlanItemService.updateAuditStatus(Long.parseLong(event.getBusinessKey()), event.getResult());
}
}
