package com.syj.eplus.module.infra.api.paymentitem;

import com.syj.eplus.module.infra.api.paymentitem.dto.PaymentItemDTO;
import com.syj.eplus.module.infra.service.paymentitem.PaymentItemService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Description：
 * @Author：du
 * @Date：2024/3/19 21:14
 */
@Service
public class PaymentItemApiImpl implements PaymentItemApi {

    @Resource
    private PaymentItemService paymentitemService;

    @Override
    public List<PaymentItemDTO> getPaymentDTOList(List<Long> idList) {
        return paymentitemService.getPaymentDTOListByIds(idList);
    }

    @Override
    public PaymentItemDTO getPaymentDTO(Long id) {
        return paymentitemService.getPaymentDTOById(id);
    }

    @Override
    public Map<Long, PaymentItemDTO> getPaymentDTOMap() {
        return paymentitemService.getPaymentDTOMap();
    }
}
