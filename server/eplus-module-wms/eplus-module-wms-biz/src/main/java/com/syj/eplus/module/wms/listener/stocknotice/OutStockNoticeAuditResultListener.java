package com.syj.eplus.module.wms.listener.stocknotice;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import com.syj.eplus.module.wms.service.stockNotice.StockNoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class OutStockNoticeAuditResultListener extends BpmProcessInstanceResultEventListener {
    @Resource
    private StockNoticeService stockNoticeService;
    @Override
    protected String getProcessDefinitionKey() {
        return stockNoticeService.getOutProcessDefinitionKey();
    }

    @Override
    protected void onEvent(BpmProcessInstanceResultEvent event) {
        long id = Long.parseLong(event.getBusinessKey());
        Integer result = event.getResult();
        stockNoticeService.updateAuditStatus(id, result,null);
    }
}
