package com.syj.eplus.module.sms.listener.quotation;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import com.syj.eplus.module.sms.service.quotation.QuotationService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class QuotationAuditResultListener extends BpmProcessInstanceResultEventListener {
@Resource
private QuotationService quotationService;
@Override
protected String getProcessDefinitionKey() {
return quotationService.getProcessDefinitionKey();
}

@Override
protected void onEvent(BpmProcessInstanceResultEvent event) {
     Integer result = event.getResult();
     quotationService.updateAuditStatus(Long.parseLong(event.getBusinessKey()), result, null);
  }
}