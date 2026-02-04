package com.syj.eplus.module.sms.listener.salecontract;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import com.syj.eplus.framework.common.enums.SaleContractStatusEnum;
import com.syj.eplus.module.sms.service.salecontract.SaleContractService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class FactoryContractAuditResultListener extends BpmProcessInstanceResultEventListener {
    @Resource
    private SaleContractService saleContractService;

    @Override
    protected String getProcessDefinitionKey() {
        return saleContractService.getFactoryProcessDefinitionKey();
    }

    @Override
    protected void onEvent(BpmProcessInstanceResultEvent event) {
        Integer result = event.getResult();
        if (BpmProcessInstanceResultEnum.APPROVE.getResult().equals(result)) {
            saleContractService.updateAuditStatus(Long.parseLong(event.getBusinessKey()), result, null, SaleContractStatusEnum.WAITING_FOR_COUNTERSIGNATURE.getValue());
        } else {
            saleContractService.updateAuditStatus(Long.parseLong(event.getBusinessKey()), result, null, SaleContractStatusEnum.WAITING_FOR_APPROVAL.getValue());
        }
    }
}