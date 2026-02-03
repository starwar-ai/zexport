package com.syj.eplus.module.oa.service.loanapp.listener;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceResultEventListener;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.UserBackAccountDTO;
import com.syj.eplus.framework.common.enums.BusinessSubjectTypeEnum;
import com.syj.eplus.framework.common.enums.BusinessTypeEnum;
import com.syj.eplus.module.fms.api.payment.api.payment.PaymentApi;
import com.syj.eplus.module.fms.api.payment.api.payment.dto.PaymentSaveDTO;
import com.syj.eplus.module.oa.converter.LoanConverter;
import com.syj.eplus.module.oa.dal.dataobject.loanapp.LoanAppDO;
import com.syj.eplus.module.oa.service.loanapp.LoanAppService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Component
public class LoanAppAuditResultListener extends BpmProcessInstanceResultEventListener {

    @Resource
    private LoanAppService loanAppService;

    @Resource
    private PaymentApi paymentApi;
    @Resource
    private AdminUserApi adminUserApi;

    @Override
    protected String getProcessDefinitionKey() {
        return loanAppService.getProcessDefinitionKey();
    }

    @Override
    protected void onEvent(BpmProcessInstanceResultEvent event) {
        loanAppService.updateAuditStatus(Long.parseLong(event.getBusinessKey()), event.getResult());
        if (event.getResult().equals(BpmProcessInstanceResultEnum.APPROVE.getResult())) {
            // 创建付款单
            LoanAppDO loanApp = loanAppService.getLoanAppById(Long.parseLong(event.getBusinessKey()));
            Long applyerId = loanApp.getApplyer().getUserId();
            PaymentSaveDTO paymentSaveDTO = LoanConverter.INSTANCE.convertPaymentSaveDTO(loanApp);
            paymentSaveDTO.setAuditStatus(BpmProcessInstanceResultEnum.PROCESS.getResult());
            paymentSaveDTO.setBusinessType(BusinessTypeEnum.LOAN_APP.getCode());
            paymentSaveDTO.setBusinessCode(loanApp.getCode());
            paymentSaveDTO.setBusinessSubjectCode(String.valueOf(applyerId));
            paymentSaveDTO.setBusinessSubjectType(BusinessSubjectTypeEnum.TRAVEL_REMIB.getCode());
            paymentSaveDTO.setCompanyId(loanApp.getCompanyId());
            paymentSaveDTO.setApplyAmount(Collections.singletonList(loanApp.getAmount()));
            paymentSaveDTO.setApplyer(loanApp.getApplyer());
            paymentSaveDTO.setPaymentMethod(loanApp.getLoanType());
            paymentSaveDTO.setRemark(loanApp.getPurpose());
            paymentSaveDTO.setApplyPaymentDate(loanApp.getLoanDate());
            Map<Long, UserBackAccountDTO> userBackAccountDTOMap = adminUserApi.getUserBackAccountDTOMap(applyerId);
            if (CollUtil.isNotEmpty(userBackAccountDTOMap)) {
                UserBackAccountDTO userBackAccountDTO = userBackAccountDTOMap.get(applyerId);
                if (Objects.nonNull(userBackAccountDTO)) {
                    paymentSaveDTO.setBank(userBackAccountDTO.getBank())
                            .setBankAccount(userBackAccountDTO.getBankAccount())
                            .setBankPoc(userBackAccountDTO.getBankPoc())
                            .setBankAddress(userBackAccountDTO.getBankAddress())
                            .setBankCode(userBackAccountDTO.getBankCode());
                }
            }
            paymentApi.createPayment(paymentSaveDTO);
        }
    }

}
