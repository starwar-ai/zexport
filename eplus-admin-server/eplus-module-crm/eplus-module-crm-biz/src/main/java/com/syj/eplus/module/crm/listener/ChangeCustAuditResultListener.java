package com.syj.eplus.module.crm.listener;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import com.syj.eplus.module.crm.controller.admin.cust.vo.CrmCustDetailReq;
import com.syj.eplus.module.crm.controller.admin.cust.vo.CustInfoRespVo;
import com.syj.eplus.module.crm.service.cust.CustService;
import com.syj.eplus.module.dms.enums.api.ShipmentApi;
import com.syj.eplus.module.sms.api.SaleContractApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description：
 * @Author：du
 * @Date：2024/1/29 18:00
 */
@Component
public class ChangeCustAuditResultListener extends BpmProcessInstanceResultEventListener {
    @Resource
    private CustService custService;

    @Resource
    private SaleContractApi saleContractApi;

    @Resource
    private ShipmentApi shipmentApi;

    @Override
    protected String getProcessDefinitionKey() {
        return "";
    }
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void onEvent(BpmProcessInstanceResultEvent event) {
        //新客户更新为已通过
        custService.updateChangeAuditStatus(Long.parseLong(event.getBusinessKey()), event.getResult());
        CustInfoRespVo cust = custService.getCust(new CrmCustDetailReq().setCustId(Long.parseLong(event.getBusinessKey())));
        if (event.getResult() == BpmProcessInstanceResultEnum.APPROVE.getResult() && cust.getVer() > 1) {
            custService.changeSuccess(cust);
        }
    }

    @Override
    protected String getProcessDefinitionKeyByBusinessId(Long id) {
        return custService.getProcessDefinitionKeyByBusinessId(id);
    }
}
