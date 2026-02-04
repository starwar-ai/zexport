package com.syj.eplus.module.scm.listener;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import com.syj.eplus.module.scm.service.vender.VenderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description：
 * @Author：du
 * @Date：2024/1/29 18:00
 */
@Slf4j
@Component
public class VenderAuditResultListener extends BpmProcessInstanceResultEventListener {
    @Resource
    private VenderService venderService;

    @Override
    protected String getProcessDefinitionKey() {
        return venderService.getProcessDefinitionKey();
    }

    @Override
    protected void onEvent(BpmProcessInstanceResultEvent event) {
        String businessKey = event.getBusinessKey();
        Integer result = event.getResult();
        venderService.updateAuditStatus(Long.parseLong(businessKey), result);

        // 审核通过
        if (result.intValue() == BpmProcessInstanceResultEnum.APPROVE.getResult().intValue()) {
            venderService.createVenderWareHouse(Long.valueOf(businessKey));
        }
    }
}
