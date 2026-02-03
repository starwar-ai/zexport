package com.syj.eplus.module.pjms.listener.project;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import com.syj.eplus.module.pjms.service.project.ProjectService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class ProjectAuditResultListener extends BpmProcessInstanceResultEventListener {
@Resource
private ProjectService projectService;
@Override
protected String getProcessDefinitionKey() {
return projectService.getProcessDefinitionKey();
}

@Override
protected void onEvent(BpmProcessInstanceResultEvent event) {
projectService.updateAuditStatus(Long.parseLong(event.getBusinessKey()), event.getResult());
}
}
