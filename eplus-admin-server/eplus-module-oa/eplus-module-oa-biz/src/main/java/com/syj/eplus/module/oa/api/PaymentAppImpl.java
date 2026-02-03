package com.syj.eplus.module.oa.api;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.syj.eplus.framework.common.entity.PaymentAppDTO;
import com.syj.eplus.module.oa.api.dto.SimplePaymentAppDTO;
import com.syj.eplus.module.oa.dal.dataobject.paymentapp.PaymentAppDO;
import com.syj.eplus.module.oa.service.paymentapp.PaymentAppService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class PaymentAppImpl implements PaymentAppApi {
    @Resource
    private PaymentAppService paymentAppService;

    @Override
    public void updatePaymentApp(PaymentAppDTO paymentAppDTO) {
        paymentAppService.updatePaymentApp(paymentAppDTO);
    }

    @Override
    public SimplePaymentAppDTO getPaymentApp(String code) {
        PaymentAppDO paymentAppDo = paymentAppService.getPaymentAppByCode(code);
        return BeanUtils.toBean(paymentAppDo, SimplePaymentAppDTO.class);

    }

    @Override
    public void batchUpdatePaymentApp(List<PaymentAppDTO> paymentAppDTOList) {
        paymentAppService.batchUpdatePaymentApp(paymentAppDTOList);
    }

    @Override
    public boolean validateRelationCodeExists(Collection<String> relationCodes) {
        return paymentAppService.validateRelationCodeExists(relationCodes);
    }

    @Override
    public boolean validateCustExists(String custCode) {
        return paymentAppService.validateCustExists(custCode);
    }

    @Override
    public Map<String,PaymentAppDTO> getSimplePaymentMsgByCodes(Collection<String> codes) {
        if (CollUtil.isEmpty(codes)){
            return Map.of();
        }
        return paymentAppService.getSimplePaymentMsgByCodes(codes);
    }
}
