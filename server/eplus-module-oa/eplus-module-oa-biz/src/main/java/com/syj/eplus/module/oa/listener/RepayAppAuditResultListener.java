package com.syj.eplus.module.oa.listener;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.enums.BusinessSubjectTypeEnum;
import com.syj.eplus.framework.common.enums.ReceiptBusinessType;
import com.syj.eplus.module.fms.api.payment.api.receipt.ReceiptApi;
import com.syj.eplus.module.fms.api.payment.api.receipt.dto.ReceiptCreateDTO;
import com.syj.eplus.module.oa.controller.admin.repayapp.vo.RepayAppDetailReq;
import com.syj.eplus.module.oa.controller.admin.repayapp.vo.RepayAppRespVO;
import com.syj.eplus.module.oa.convert.repayapp.RepayAppConvert;
import com.syj.eplus.module.oa.service.repayapp.RepayAppService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.bpm.enums.ErrorCodeConstants.STARAT_USER_NOT_NULL;
import static com.syj.eplus.module.oa.enums.ErrorCodeConstants.REPAY_APP_NOT_EXISTS;
import static com.syj.eplus.module.oa.enums.ErrorCodeConstants.REPAY_USER_NOT_NULL;

/**
 * @Description：还款单审核监听类
 * @Author：du
 * @Date：2024/4/19 11:19
 */
@Component
public class RepayAppAuditResultListener extends BpmProcessInstanceResultEventListener {
    @Resource
    private RepayAppService repayAppService;
    @Resource
    private ReceiptApi receiptApi;

    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;
    @Override
    protected String getProcessDefinitionKey() {
        return repayAppService.getProcessDefinitionKey();
    }

    @Override
    protected void onEvent(BpmProcessInstanceResultEvent event) {
        long repayAppId = Long.parseLong(event.getBusinessKey());
        // 更新审核状态
        repayAppService.updateAuditStatus(repayAppId, event.getResult());
        if (!BpmProcessInstanceResultEnum.APPROVE.getResult().equals(event.getResult())){
            return;
        }
        // 获取还款信息
        RepayAppRespVO repayAppRespVO = repayAppService.getRepayApp(new RepayAppDetailReq().setRepayAppId(repayAppId));
        if (Objects.isNull(repayAppRespVO)){
            throw exception(REPAY_APP_NOT_EXISTS);
        }
        UserDept repayer = repayAppRespVO.getRepayer();
        if (Objects.isNull(repayer) || Objects.isNull(repayer.getUserId())) {
            throw exception(REPAY_USER_NOT_NULL);
        }
        // 创建收款单
        ReceiptCreateDTO receiptCreateDTO = RepayAppConvert.INSTANCE.convertReceiptCreateDTO(repayAppRespVO);
        receiptCreateDTO.setBusinessType(ReceiptBusinessType.REPAY.getValue());
        receiptCreateDTO.setBusinessCode(repayAppRespVO.getCode());
        receiptCreateDTO.setBusinessSubjectType(BusinessSubjectTypeEnum.TRAVEL_REMIB.getCode());
        receiptCreateDTO.setBusinessSubjectCode(String.valueOf(repayer.getUserId()));
        receiptCreateDTO.setCompanyId(repayAppRespVO.getCompanyId());
        receiptCreateDTO.setReceiptType(repayAppRespVO.getRepayType());
        receiptCreateDTO.setBusinessSubjectCode(String.valueOf(repayer.getUserId()));
        // 将收款流程发起人改为还款人
        Long startUserIdByProcessInstanceId = bpmProcessInstanceApi.getStartUserIdByProcessInstanceId(event.getId());
        if (Objects.isNull(startUserIdByProcessInstanceId)) {
            throw exception(STARAT_USER_NOT_NULL);
        }
        receiptCreateDTO.setStartUserId(startUserIdByProcessInstanceId);
        receiptApi.createReceipt(receiptCreateDTO);
    }
}
