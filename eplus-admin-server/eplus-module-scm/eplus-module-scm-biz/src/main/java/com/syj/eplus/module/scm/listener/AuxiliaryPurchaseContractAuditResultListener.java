package com.syj.eplus.module.scm.listener;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import com.syj.eplus.module.scm.service.purchasecontract.PurchaseContractService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class AuxiliaryPurchaseContractAuditResultListener extends BpmProcessInstanceResultEventListener {
    @Resource
    private PurchaseContractService purchaseContractService;
    @Override
    protected String getProcessDefinitionKey() {
        return purchaseContractService.getAuxiliaryProcessDefinitionKey();
    }

    @Override
    protected void onEvent(BpmProcessInstanceResultEvent event) {
        purchaseContractService.updateAuditStatus(Long.parseLong(event.getBusinessKey()), event.getResult(),null);
    }
}
