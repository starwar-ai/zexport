package com.syj.eplus.module.sms.listener.saleauxiliaryallocation;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import com.syj.eplus.module.sms.service.saleauxiliaryallocation.SaleAuxiliaryAllocationService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class SaleAuxiliaryAllocationAuditResultListener extends BpmProcessInstanceResultEventListener {
@Resource
private SaleAuxiliaryAllocationService saleAuxiliaryAllocationService;
@Override
protected String getProcessDefinitionKey() {
return saleAuxiliaryAllocationService.getProcessDefinitionKey();
}

@Override
protected void onEvent(BpmProcessInstanceResultEvent event) {
saleAuxiliaryAllocationService.updateAuditStatus(Long.parseLong(event.getBusinessKey()), event.getResult());
}
}
