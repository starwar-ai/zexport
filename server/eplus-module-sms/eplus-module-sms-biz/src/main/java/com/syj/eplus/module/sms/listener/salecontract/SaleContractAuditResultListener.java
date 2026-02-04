//package com.syj.eplus.module.sms.listener.salecontract;

//
//import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
//import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
//import com.syj.eplus.module.sms.service.salecontract.SaleContractService;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//
//
//@Component
//public class SaleContractAuditResultListener extends BpmProcessInstanceResultEventListener {
//    @Resource
//    private SaleContractService saleContractService;
//
//    @Override
//    protected String getProcessDefinitionKey() {
//        return saleContractService.getProcessDefinitionKey();
//    }
//
//    @Override
//    protected void onEvent(BpmProcessInstanceResultEvent event) {
//        saleContractService.updateAuditStatus(Long.parseLong(event.getBusinessKey()), event.getResult(), null);
//    }
//}
