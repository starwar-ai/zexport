package com.syj.eplus.module.pms.listener.sku;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import com.syj.eplus.module.pms.service.sku.SkuService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/09/26/17:43
 * @Description:
 */
@Component
public class OwnBrandAduitResultListener extends BpmProcessInstanceResultEventListener {
    @Resource
    private SkuService skuService;

    @Override
    protected String getProcessDefinitionKey() {
        return skuService.getOwnBrandProcessDefinitionKey();
    }

    @Override
    protected void onEvent(BpmProcessInstanceResultEvent event) {
        skuService.updateAuditStatus(Long.parseLong(event.getBusinessKey()), event.getResult(),null);
    }
}
