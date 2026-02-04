package com.syj.eplus.module.scm.listener.purchaseregistration;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import com.syj.eplus.framework.common.enums.PurchaseRegistrationStatusEnum;
import com.syj.eplus.module.scm.service.purchaseregistration.PurchaseRegistrationService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class PurchaseRegistrationAuditResultListener extends BpmProcessInstanceResultEventListener {
    @Resource
    private PurchaseRegistrationService purchaseRegistrationService;

    @Override
    protected String getProcessDefinitionKey() {
        return purchaseRegistrationService.getProcessDefinitionKey();
    }

    @Override
    protected void onEvent(BpmProcessInstanceResultEvent event) {
        Integer result = event.getResult();
        if (BpmProcessInstanceResultEnum.APPROVE.getResult().equals(result)) {
            purchaseRegistrationService.updateAuditStatus(Long.parseLong(event.getBusinessKey()), result, null, PurchaseRegistrationStatusEnum.APPROVALED.getValue());
        }else{
            purchaseRegistrationService.updateAuditStatus(Long.parseLong(event.getBusinessKey()), result, null, PurchaseRegistrationStatusEnum.WAITING_FOR_APPROVAL.getValue());
        }
    }
}
