package com.syj.eplus.module.oa.api;


import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.PaymentAppDTO;
import com.syj.eplus.module.oa.api.dto.SimpleLoanAppRespDTO;
import com.syj.eplus.module.oa.controller.admin.loanapp.vo.LoanAppDetailReq;
import com.syj.eplus.module.oa.service.loanapp.LoanAppService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Service
public class LoanAppApiImpl implements LoanAppApi {

    @Lazy
    @Resource
    private LoanAppService loanAppService;

    @Override
    public List<SimpleLoanAppRespDTO> getSimpleLoanAppRespDTOList(List<Long> idList, Long travelReimbId, Long repayAppId) {

        return loanAppService.getSimpleLoanAppRespDTOList(idList, travelReimbId, repayAppId);
    }

    @Override
    public SimpleLoanAppRespDTO getSimpleLoanAppRespDTOById(Long id) {
        SimpleLoanAppRespDTO simpleLoanAppRespDTO = loanAppService.getsimpleLoanAppDTO(new LoanAppDetailReq().setLoanAppId(id));
        if (Objects.isNull(simpleLoanAppRespDTO)) {
            return null;
        }
        return simpleLoanAppRespDTO;
    }

    @Override
    public List<Long> getIdListByCode(String code) {
        return loanAppService.getIdListByCode(code);
    }

    @Override
    public void updatePaymentStatus(PaymentAppDTO paymentAppDTO) {
        loanAppService.updatePaymentStatus(paymentAppDTO);
    }

    @Override
    public void updateRepayAmount(JsonAmount repayAmount, Long id) {
        loanAppService.updateRepayAmount(repayAmount, id);
    }

    @Override
    public void batchUpdaPaymentStatus(List<PaymentAppDTO> paymentAppDTOList) {
        loanAppService.batchUpdaPaymentStatus(paymentAppDTOList);
    }
}
