package com.syj.eplus.module.scm.listener;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import com.syj.eplus.module.scm.service.purchasecontract.PurchaseContractService;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/08/10:17
 * @Description:
 */
public class PurchaseContractOwnBrandAuditResultListener extends BpmProcessInstanceResultEventListener {
    @Resource
    private PurchaseContractService purchaseContractService;

    @Override
    protected String getProcessDefinitionKey() {
        return purchaseContractService.getProcessDefinitionOwnBrandKey();
    }

    @Override
    protected void onEvent(BpmProcessInstanceResultEvent event) {
        purchaseContractService.updateAuditStatus(Long.parseLong(event.getBusinessKey()), event.getResult(),null);
    }
}
