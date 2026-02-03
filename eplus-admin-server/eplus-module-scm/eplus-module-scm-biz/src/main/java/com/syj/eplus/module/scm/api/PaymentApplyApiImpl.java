package com.syj.eplus.module.scm.api;

import com.syj.eplus.framework.common.entity.PaymentAppDTO;
import com.syj.eplus.module.scm.api.paymentapply.PaymentApplyApi;
import com.syj.eplus.module.scm.api.paymentapply.dto.PaymentApplyDTO;
import com.syj.eplus.module.scm.service.paymentapply.PaymentApplyService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/09/17:06
 * @Description:
 */
@Service
public class PaymentApplyApiImpl implements PaymentApplyApi {

    @Resource
    private PaymentApplyService paymentApplyService;

    @Override
    public void batchWriteBackPaymentMsg(List<PaymentAppDTO> paymentAppDTOList) {
        paymentApplyService.batchWriteBackPaymentMsg(paymentAppDTOList);
    }

    @Override
    public PaymentApplyDTO getPaymentApply(String code) {
        return paymentApplyService.getPaymentApplyByCode(code);
    }

    @Override
    public void batchWriteAnnex(List<PaymentAppDTO> paymentAppDTOList) {
        paymentApplyService.batchWriteAnnex(paymentAppDTOList);
    }
}
