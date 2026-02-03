package com.syj.eplus.module.fms.listener.payment;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import com.syj.eplus.module.fms.service.payment.PaymentService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description：
 * @Author：du
 * @Date：2024/1/29 18:00
 */
@Component
public class PaymentAuditResultListener extends BpmProcessInstanceResultEventListener {
    @Resource
    private PaymentService paymentService;
    @Override
    protected String getProcessDefinitionKey() {
        return paymentService.getProcessDefinitionKey();
    }

    @Override
    protected void onEvent(BpmProcessInstanceResultEvent event) {
        paymentService.updateAuditStatus(Long.parseLong(event.getBusinessKey()), event.getResult());
        paymentService.updateApprovalMessage(Long.parseLong(event.getBusinessKey()), event.getId());

    }
}
