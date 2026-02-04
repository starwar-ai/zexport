package com.syj.eplus.module.scm.listener.paymentapply;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import com.syj.eplus.module.scm.service.paymentapply.PaymentApplyService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class PaymentApplyAuditResultListener extends BpmProcessInstanceResultEventListener {
    @Resource
    private PaymentApplyService paymentApplyService;

    @Override
    protected String getProcessDefinitionKey() {
        return paymentApplyService.getProcessDefinitionKey();
    }

    @Override
    protected void onEvent(BpmProcessInstanceResultEvent event) {
        long id = Long.parseLong(event.getBusinessKey());
        // 更新审核状态
        paymentApplyService.updateAuditStatus(id, event.getResult(),null);
        if (BpmProcessInstanceResultEnum.APPROVE.getResult().equals(event.getResult())) {
            // 创建付款单
            paymentApplyService.createPaymentOrder(id);
            // 更改采购合同付款计划状态
        }else {
            // 更改采购合同付款计划状态
        }

    }
}
