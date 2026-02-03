package com.syj.eplus.module.dtms.listener.design;

import cn.iocoder.yudao.framework.operatelog.core.util.OperateLogUtils;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.syj.eplus.module.dtms.dal.dataobject.design.DesignDO;
import com.syj.eplus.module.dtms.enums.DesignStatusEnum;
import com.syj.eplus.module.dtms.service.design.DesignService;
import com.syj.eplus.module.dtms.service.design.DesignServiceImpl;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class DesignAuditResultListener extends BpmProcessInstanceResultEventListener {
    @Resource
    private DesignService designService;
    @Resource
    private AdminUserApi adminUserApi;

    @Override
    protected String getProcessDefinitionKey() {
        return designService.getProcessDefinitionKey();
    }

    @Override
    protected void onEvent(BpmProcessInstanceResultEvent event) {
        Long designId = Long.parseLong(String.valueOf(event.getBusinessKey()));
        Integer result = event.getResult();
        AdminUserRespDTO currentUser = adminUserApi.getUser(WebFrameworkUtils.getLoginUserId());
        DesignDO designDO = designService.getById(designId);
        designDO.setAuditStatus(result);
        // 审核通过
        if (result.intValue() == BpmProcessInstanceResultEnum.APPROVE.getResult().intValue()) {
            designDO.setDesignStatus(DesignStatusEnum.IN_PROCESS.getValue());
            OperateLogUtils.setContent(String.format("【设计任务】%s", currentUser.getNickname()+"审核通过"));
        }else {
            designDO.setDesignStatus(DesignStatusEnum.REJECTED.getValue());
            OperateLogUtils.setContent(String.format("【设计任务】%s", currentUser.getNickname()+"审核拒绝"));
        }
        OperateLogUtils.addExt(DesignServiceImpl.OPERATOR_EXTS_KEY, designDO.getCode());
        designService.updateById(designDO);
    }
}
