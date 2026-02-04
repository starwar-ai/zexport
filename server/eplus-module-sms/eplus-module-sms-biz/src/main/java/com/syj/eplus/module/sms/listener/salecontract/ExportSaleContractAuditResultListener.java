package com.syj.eplus.module.sms.listener.salecontract;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import com.syj.eplus.framework.common.enums.SaleContractStatusEnum;
import com.syj.eplus.module.sms.service.salecontract.SaleContractService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/16 17:39
 */
@Component
public class ExportSaleContractAuditResultListener extends BpmProcessInstanceResultEventListener {
    @Resource
    private SaleContractService saleContractService;

    @Override
    protected String getProcessDefinitionKey() {
        return saleContractService.getExportProcessDefinitionKey();
    }

    @Override
    protected void onEvent(BpmProcessInstanceResultEvent event) {
        Integer result = event.getResult();
        if (BpmProcessInstanceResultEnum.APPROVE.getResult().equals(result)) {
            saleContractService.updateAuditStatus(Long.parseLong(event.getBusinessKey()), result, null, SaleContractStatusEnum.WAITING_FOR_COUNTERSIGNATURE.getValue());
        } else if (BpmProcessInstanceResultEnum.CANCEL.getResult().equals(result)){
            saleContractService.updateAuditStatus(Long.parseLong(event.getBusinessKey()), result, null, SaleContractStatusEnum.WAITING_FOR_SUBMISSION.getValue());
        } else {
            saleContractService.updateAuditStatus(Long.parseLong(event.getBusinessKey()), result, null, SaleContractStatusEnum.WAITING_FOR_APPROVAL.getValue());
        }
    }
}
