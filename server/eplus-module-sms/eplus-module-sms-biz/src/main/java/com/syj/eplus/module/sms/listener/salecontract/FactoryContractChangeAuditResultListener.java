package com.syj.eplus.module.sms.listener.salecontract;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import com.syj.eplus.module.sms.service.salecontract.SaleContractService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class FactoryContractChangeAuditResultListener extends BpmProcessInstanceResultEventListener {
    @Resource
    private SaleContractService saleContractService;

    @Override
    protected String getProcessDefinitionKey() {
        return "";
    }

    @Override
    protected void onEvent(BpmProcessInstanceResultEvent event) {
        saleContractService.updateChangeAuditStatus(Long.parseLong(event.getBusinessKey()), event.getResult(), null);
        // 更新影响范围状态
        if (BpmProcessInstanceResultEnum.APPROVE.getResult().equals(event.getResult())) {
            saleContractService.updateEffectRanageStatus(Long.parseLong(event.getBusinessKey()));
        } if (BpmProcessInstanceResultEnum.BACK.getResult().equals(event.getResult())||BpmProcessInstanceResultEnum.REJECT.getResult().equals(event.getResult())||BpmProcessInstanceResultEnum.CANCEL.getResult().equals(event.getResult())){
            // 将原合同状态改为未变更
            saleContractService.updateContractChangeStatus(Long.parseLong(event.getBusinessKey()));
        }
    }

    @Override
    protected String getProcessDefinitionKeyByBusinessId(Long id) {
        return saleContractService.getProcessDefinitionKeyByBusinessId(id);
    }
}