package com.syj.eplus.module.pms.listener.sku;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import com.syj.eplus.module.pms.controller.admin.sku.vo.SkuDetailReq;
import com.syj.eplus.module.pms.controller.admin.sku.vo.SkuInfoRespVO;
import com.syj.eplus.module.pms.service.sku.SkuService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description：
 * @Author：du
 * @Date：2024/5/17 15:42
 */
@Component
public class ChangeCskuAuditResultListener extends BpmProcessInstanceResultEventListener {
    @Resource
    private SkuService skuService;

    @Override
    protected String getProcessDefinitionKey() {
        return skuService.getChangeCskuProcessDefinitionKey();
    }

    @Override
    protected void onEvent(BpmProcessInstanceResultEvent event) {
        skuService.updateChangeAuditStatus(Long.parseLong(event.getBusinessKey()), event.getResult(),null);
        SkuInfoRespVO sku = skuService.getSku(new SkuDetailReq().setSkuId(Long.parseLong(event.getBusinessKey())));
        if (event.getResult() == BpmProcessInstanceResultEnum.APPROVE.getResult() && sku.getVer() > 1) {
            skuService.changeSuccess(sku);
        }
    }
}
