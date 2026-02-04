package com.syj.eplus.module.pms.listener.packagetype;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import com.syj.eplus.module.pms.service.packagetype.PackageTypeService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class PackageTypeAuditResultListener extends BpmProcessInstanceResultEventListener {
@Resource
private PackageTypeService packageTypeService;
@Override
protected String getProcessDefinitionKey() {
return packageTypeService.getProcessDefinitionKey();
}

@Override
protected void onEvent(BpmProcessInstanceResultEvent event) {
packageTypeService.updateAuditStatus(Long.parseLong(event.getBusinessKey()), event.getResult());
}
}
