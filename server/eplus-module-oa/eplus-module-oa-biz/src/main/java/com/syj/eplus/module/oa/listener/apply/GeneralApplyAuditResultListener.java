package com.syj.eplus.module.oa.listener.apply;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import com.syj.eplus.framework.common.enums.ReimbTypeEnum;
import com.syj.eplus.module.oa.service.apply.ApplyService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class GeneralApplyAuditResultListener extends BpmProcessInstanceResultEventListener {
    @Resource
    private ApplyService applyService;
    @Override
    protected String getProcessDefinitionKey() {
        return applyService.getProcessDefinitionKey(ReimbTypeEnum.GENERAL.getValue());
    }

    @Override
    protected void onEvent(BpmProcessInstanceResultEvent event) {
        applyService.updateAuditStatus(Long.parseLong(event.getBusinessKey()), event.getResult());
    }
}