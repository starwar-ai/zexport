package com.syj.eplus.module.qms.listener.qualityinspection;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import com.syj.eplus.framework.common.enums.InspectionBillStatusEnum;
import com.syj.eplus.module.qms.dal.dataobject.qualityinspection.QualityInspectionDO;
import com.syj.eplus.module.qms.service.qualityinspection.QualityInspectionService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class QualityInspectionAuditResultListener extends BpmProcessInstanceResultEventListener {
    @Resource
    private QualityInspectionService qualityInspectionService;

    @Override
    protected String getProcessDefinitionKey() {
        return qualityInspectionService.getProcessDefinitionKey();
    }

    @Override
    protected void onEvent(BpmProcessInstanceResultEvent event) {
        String businessKey = event.getBusinessKey();
        QualityInspectionDO qualityInspectionDO = qualityInspectionService.getById(businessKey);
        Integer result = event.getResult();
        qualityInspectionDO.setAuditStatus(result);

        if (result.intValue() == BpmProcessInstanceResultEnum.APPROVE.getResult().intValue()) {
            qualityInspectionDO.setQualityInspectionStatus(InspectionBillStatusEnum.WAITING_FOR_CONFIRMATION.getValue());
        }else if (result.intValue() == BpmProcessInstanceResultEnum.REJECT.getResult().intValue()) {
            qualityInspectionDO.setQualityInspectionStatus(InspectionBillStatusEnum.REJECTED.getValue());
        }
        qualityInspectionService.updateById(qualityInspectionDO);
    }
}
