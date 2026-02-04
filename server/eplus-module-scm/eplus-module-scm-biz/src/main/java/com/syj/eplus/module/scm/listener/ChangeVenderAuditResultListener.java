package com.syj.eplus.module.scm.listener;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import com.syj.eplus.module.dms.enums.api.ShipmentApi;
import com.syj.eplus.module.scm.api.purchasecontract.PurchaseContractApi;
import com.syj.eplus.module.scm.controller.admin.vender.vo.ScmVenderDetailReq;
import com.syj.eplus.module.scm.controller.admin.vender.vo.VenderInfoRespVO;
import com.syj.eplus.module.scm.service.vender.VenderService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description：
 * @Author：du
 * @Date：2024/1/29 18:00
 */
@Slf4j
@Component
public class ChangeVenderAuditResultListener extends BpmProcessInstanceResultEventListener {
    @Resource
    private VenderService venderService;

    @Resource
    private PurchaseContractApi purchaseContractApi;

    @Resource
    private ShipmentApi shipmentApi;

    @Override
    protected String getProcessDefinitionKey() {
        return venderService.getChangeProcessDefinitionKey();
    }

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void onEvent(BpmProcessInstanceResultEvent event) {
        String businessKey = event.getBusinessKey();
        Integer result = event.getResult();
        venderService.updateChangeAuditStatus(Long.parseLong(businessKey), result);

        VenderInfoRespVO vender = venderService.getVender(new ScmVenderDetailReq().setVenderId(Long.parseLong(event.getBusinessKey())));
        if (event.getResult() == BpmProcessInstanceResultEnum.APPROVE.getResult() && vender.getVer() > 1) {
            venderService.changeSuccess(vender);
        }
    }
}
