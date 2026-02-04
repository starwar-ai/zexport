package com.syj.eplus.module.scm.listener;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.module.scm.service.purchasecontract.PurchaseContractService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description：
 * @Author：du
 * @Date：2024/5/23 15:47
 */
@Component
public class PurchaseContractChangeListener extends BpmProcessInstanceResultEventListener {
    @Resource
    private PurchaseContractService purchaseContractService;

    @Override
    protected String getProcessDefinitionKey() {
        return "";
    }

    @Override
    protected void onEvent(BpmProcessInstanceResultEvent event) {
        purchaseContractService.updateChangeAuditStatus(Long.parseLong(event.getBusinessKey()), event.getResult(), null);
        // 更新影响范围状态
        if (BpmProcessInstanceResultEnum.APPROVE.getResult().equals(event.getResult())) {
            purchaseContractService.updateEffectRanageStatus(Long.parseLong(event.getBusinessKey()));
        }else if (BpmProcessInstanceResultEnum.CANCEL.getResult().equals(event.getResult())){
            // 将原合同状态改为未变更
            purchaseContractService.updateChangeStatus(Long.parseLong(event.getBusinessKey()), BooleanEnum.NO.getValue());
        }
    }

    @Override
    protected String getProcessDefinitionKeyByBusinessId(Long id) {
        return purchaseContractService.getProcessDefinitionKeyByBusinessId(id);
    }
}