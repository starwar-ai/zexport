package com.syj.eplus.module.oa.listener;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.module.oa.service.feeshare.FeeShareService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description：
 * @Author：du
 * @Date：2024/1/29 18:00
 */
@Component
public class FeeShareAuditResultListener extends BpmProcessInstanceResultEventListener {
    @Resource
    private FeeShareService feeShareService;

    @Override
    protected String getProcessDefinitionKey() {
        return feeShareService.getProcessDefinitionKey();
    }

    @Override
    protected void onEvent(BpmProcessInstanceResultEvent event) {
        String businessKey = event.getBusinessKey();
        feeShareService.updateAuditStatus(businessKey, event.getResult(),null);
        // 更新来源单据归属状态
        if (BpmProcessInstanceResultEnum.APPROVE.getResult().equals(event.getResult())){
            feeShareService.updateSourceFeeShareStatus(businessKey);
            feeShareService.updateStatus(businessKey, BooleanEnum.YES.getValue());
        }
    }
}
