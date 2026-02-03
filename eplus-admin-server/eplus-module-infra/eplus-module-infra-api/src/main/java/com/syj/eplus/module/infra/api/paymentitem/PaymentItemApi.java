package com.syj.eplus.module.infra.api.paymentitem;

import com.syj.eplus.module.infra.api.paymentitem.dto.PaymentItemDTO;

import java.util.List;
import java.util.Map;

/**
 * @Description：
 * @Author：du
 * @Date：2024/3/19 21:09
 */
public interface PaymentItemApi {
    /**
     * 根据支付方式编号批量获取支付方式
     *
     * @param idList 支付方式id列表
     * @return 支付方式列表
     */
    List<PaymentItemDTO> getPaymentDTOList(List<Long> idList);

    /**
     * 根据支付方式编号获取支付方式
     *
     * @param id 支付方式id
     * @return 支付方式
     */
    PaymentItemDTO getPaymentDTO(Long id);

    /**
     * 获取付款方式缓存
     * @return 付款方式缓存
     */
    Map<Long, PaymentItemDTO> getPaymentDTOMap();

}
