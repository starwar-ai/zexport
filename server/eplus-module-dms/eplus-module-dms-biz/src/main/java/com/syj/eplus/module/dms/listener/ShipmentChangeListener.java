package com.syj.eplus.module.dms.listener;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import com.syj.eplus.module.dms.service.shipment.ShipmentService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/30/18:17
 * @Description:
 */
@Component
public class ShipmentChangeListener extends BpmProcessInstanceResultEventListener {

    @Resource
    private ShipmentService shipmentService;

    @Override
    protected String getProcessDefinitionKey() {
        return "";
    }

    @Override
    protected String getProcessDefinitionKeyByBusinessId(Long id) {
        return shipmentService.getProcessDefinitionKeyByBusinessId(id);
    }

    @Override
    protected void onEvent(BpmProcessInstanceResultEvent event) {
        shipmentService.updateChangeAuditStatus(Long.parseLong(event.getBusinessKey()), event.getResult(), null);
    }
}
