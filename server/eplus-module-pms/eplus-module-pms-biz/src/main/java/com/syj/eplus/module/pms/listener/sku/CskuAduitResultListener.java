package com.syj.eplus.module.pms.listener.sku;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import com.syj.eplus.module.pms.service.sku.SkuService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description：
 * @Author：du
 * @Date：2024/5/17 15:42
 */
@Component
public class CskuAduitResultListener extends BpmProcessInstanceResultEventListener {
    @Resource
    private SkuService skuService;

    @Override
    protected String getProcessDefinitionKey() {
        return skuService.getCskuProcessDefinitionKey();
    }

    @Override
    protected void onEvent(BpmProcessInstanceResultEvent event) {
        skuService.updateAuditStatus(Long.parseLong(event.getBusinessKey()), event.getResult(),null);
    }
}
