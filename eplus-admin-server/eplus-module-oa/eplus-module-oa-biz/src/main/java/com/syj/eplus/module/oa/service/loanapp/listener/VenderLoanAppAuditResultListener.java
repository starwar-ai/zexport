package com.syj.eplus.module.oa.service.loanapp.listener;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import com.syj.eplus.framework.common.enums.BusinessSubjectTypeEnum;
import com.syj.eplus.framework.common.enums.BusinessTypeEnum;
import com.syj.eplus.framework.common.enums.VenderLoanTypeEnum;
import com.syj.eplus.module.fms.api.payment.api.payment.PaymentApi;
import com.syj.eplus.module.fms.api.payment.api.payment.dto.PaymentSaveDTO;
import com.syj.eplus.module.oa.converter.LoanConverter;
import com.syj.eplus.module.oa.dal.dataobject.loanapp.LoanAppDO;
import com.syj.eplus.module.oa.service.loanapp.LoanAppService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Objects;

@Component
public class VenderLoanAppAuditResultListener extends BpmProcessInstanceResultEventListener {

    @Resource
    private LoanAppService loanAppService;

    @Resource
    private PaymentApi paymentApi;

    @Override
    protected String getProcessDefinitionKey() {
        return loanAppService.getVenderProcessDefinitionKey();
    }

    @Override
    protected void onEvent(BpmProcessInstanceResultEvent event) {
        loanAppService.updateAuditStatus(Long.parseLong(event.getBusinessKey()), event.getResult());
        if (event.getResult().equals(BpmProcessInstanceResultEnum.APPROVE.getResult())) {
            // 创建付款单
            LoanAppDO loanApp = loanAppService.getLoanAppById(Long.parseLong(event.getBusinessKey()));
            //退款不生产单据
            if(Objects.equals(loanApp.getLoanSource(), VenderLoanTypeEnum.RETURN.getValue())){
                return;
            }
            PaymentSaveDTO paymentSaveDTO = LoanConverter.INSTANCE.convertPaymentSaveDTO(loanApp);
            paymentSaveDTO.setAuditStatus(BpmProcessInstanceResultEnum.PROCESS.getResult());
            paymentSaveDTO.setBusinessType(BusinessTypeEnum.LOAN_APP.getCode());
            paymentSaveDTO.setBusinessCode(loanApp.getCode());
            paymentSaveDTO.setBusinessSubjectCode(String.valueOf(loanApp.getApplyer().getUserId()));
            paymentSaveDTO.setBusinessSubjectType(BusinessSubjectTypeEnum.TRAVEL_REMIB.getCode());
            paymentSaveDTO.setCompanyId(loanApp.getCompanyId());
            paymentSaveDTO.setApplyAmount(Collections.singletonList(loanApp.getAmount()));
            paymentSaveDTO.setApplyer(loanApp.getApplyer());
            paymentSaveDTO.setApplyPaymentDate(loanApp.getLoanDate());
            paymentApi.createPayment(paymentSaveDTO);
        }
    }

}
