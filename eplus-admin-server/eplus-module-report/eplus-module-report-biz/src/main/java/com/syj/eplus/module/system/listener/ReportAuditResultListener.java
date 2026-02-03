package com.syj.eplus.module.system.listener;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import com.syj.eplus.module.system.service.report.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description：
 * @Author：chengbo
 * @Date：2024/1/29 18:00
 */
@Slf4j
@Component
public class ReportAuditResultListener extends BpmProcessInstanceResultEventListener {
    @Resource
    private ReportService reportService;

    @Override
    protected String getProcessDefinitionKey() {
        return reportService.getProcessDefinitionKey();
    }

    @Override
    protected void onEvent(BpmProcessInstanceResultEvent event) {
        String businessKey = event.getBusinessKey();
        Integer result = event.getResult();
        reportService.updateAuditStatus(Long.parseLong(businessKey), result);
    }
}
