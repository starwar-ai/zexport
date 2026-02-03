package com.syj.eplus.module.fms.api.payment;

import cn.hutool.core.collection.CollUtil;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.enums.SubmitFlagEnum;
import com.syj.eplus.module.fms.api.payment.api.payment.PaymentApi;
import com.syj.eplus.module.fms.api.payment.api.payment.dto.ClosePaymentDTO;
import com.syj.eplus.module.fms.api.payment.api.payment.dto.PaymentDTO;
import com.syj.eplus.module.fms.api.payment.api.payment.dto.PaymentSaveDTO;
import com.syj.eplus.module.fms.controller.admin.payment.vo.PaymentSaveReqVO;
import com.syj.eplus.module.fms.convert.PaymentConvert;
import com.syj.eplus.module.fms.service.payment.PaymentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class PaymentApiImpl implements PaymentApi {

    @Resource
    private PaymentService paymentService;

    @Override
    public void createPayment(PaymentSaveDTO paymentSaveDTO) {
        if (Objects.isNull(paymentSaveDTO)){
            return;
        }
        List<JsonAmount> applyAmount = paymentSaveDTO.getApplyAmount();
        if (CollUtil.isEmpty(applyAmount)){
            return;
        }
        boolean match = applyAmount.stream().allMatch(s -> Objects.isNull(s) || Objects.isNull(s.getAmount()) || s.getAmount().compareTo(BigDecimal.ZERO) == 0);
        if (match){
            return;
        }
        PaymentSaveReqVO paymentSaveReqVO = PaymentConvert.INSTANCE.convertPaymentSaveReqVO(paymentSaveDTO);
        UserDept applyer = paymentSaveDTO.getApplyer();
        if (Objects.nonNull(applyer)) {
            paymentSaveReqVO.setApplyerId(applyer.getUserId());
        }
        paymentSaveReqVO.setSubmitFlag(SubmitFlagEnum.SUBMIT.getStatus());
        paymentService.createPayment(paymentSaveReqVO);

    }

    @Override
    public void batchCreatePayment(List<PaymentSaveDTO> paymentSaveDTOList) {
        List<PaymentSaveReqVO> paymentSaveReqVOList = PaymentConvert.INSTANCE.convertPaymentSaveReqVOList(paymentSaveDTOList);
        paymentService.batchCreatePayment(paymentSaveReqVOList);
    }

    @Override
    public Long getPaymentNumByPurchaseContractCode(Integer businessType,String code) {
        return paymentService.getPaymentNumByPurchaseContractCode(businessType, code);
    }

    @Override
    public void validatePayment(Integer businessType, String businessCode) {
        paymentService.validatePayment(businessType, businessCode);
    }

    @Override
    public Map<String,PaymentDTO> getListByCodes(List<String> applyCodes) {
        return paymentService.getListByCodes(applyCodes);
    }

    @Override
    public void closePayment(ClosePaymentDTO closePaymentDTO) {
        paymentService.closePayment(closePaymentDTO);
    }
}
