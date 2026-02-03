package com.syj.eplus.module.pms.listener.sku;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import com.syj.eplus.module.pms.controller.admin.sku.vo.SkuDetailReq;
import com.syj.eplus.module.pms.controller.admin.sku.vo.SkuInfoRespVO;
import com.syj.eplus.module.pms.service.sku.SkuService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

@Component
public class ChangeOwnBrandAuditResultListener extends BpmProcessInstanceResultEventListener {
    @Resource
    private SkuService skuService;

    @Override
    protected String getProcessDefinitionKey() {
        return skuService.getChangeOwnBrandProcessDefinitionKey();
    }

    @Override
    protected void onEvent(BpmProcessInstanceResultEvent event) {
        skuService.updateChangeAuditStatus(Long.parseLong(event.getBusinessKey()), event.getResult(),null);
        SkuInfoRespVO sku = skuService.getSku(new SkuDetailReq().setSkuId(Long.parseLong(event.getBusinessKey())));
        if (Objects.equals(event.getResult(), BpmProcessInstanceResultEnum.APPROVE.getResult()) && sku.getVer() > 1) {
            skuService.changeSuccess(sku);
        }
    }
}
