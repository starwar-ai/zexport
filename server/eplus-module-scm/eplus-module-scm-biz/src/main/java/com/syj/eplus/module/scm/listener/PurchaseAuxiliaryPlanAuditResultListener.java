package com.syj.eplus.module.scm.listener;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import com.syj.eplus.module.scm.service.purchaseplan.PurchasePlanService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class PurchaseAuxiliaryPlanAuditResultListener extends BpmProcessInstanceResultEventListener {
    @Resource
    private PurchasePlanService purchasePlanService;
    @Override
    protected String getProcessDefinitionKey() {
        return purchasePlanService.getAuxiliaryProcessDefinitionKey();
    }

    @Override
    protected void onEvent(BpmProcessInstanceResultEvent event) {
        purchasePlanService.updateAuditStatus(Long.parseLong(event.getBusinessKey()), event.getResult());

    }
}
