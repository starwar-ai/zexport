package com.syj.eplus.module.system.listener;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import com.syj.eplus.module.system.service.report.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class ReportChangeAuditResultListener extends BpmProcessInstanceResultEventListener {
    @Resource
    private ReportService reportService;

    @Override
    protected String getProcessDefinitionKey() {
        return reportService.getChangeProcessDefinitionKey();
    }

    @Override
    protected void onEvent(BpmProcessInstanceResultEvent event) {
        String businessKey = event.getBusinessKey();
        Integer result = event.getResult();
        reportService.updateChangeAuditStatus(Long.parseLong(businessKey), result);
    }
}
