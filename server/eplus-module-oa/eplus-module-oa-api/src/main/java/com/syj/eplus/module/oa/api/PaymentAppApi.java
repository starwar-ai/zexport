package com.syj.eplus.module.oa.api;

import com.syj.eplus.framework.common.entity.PaymentAppDTO;
import com.syj.eplus.module.oa.api.dto.SimplePaymentAppDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface PaymentAppApi {

    /**
     * 更新公对公申请单付款信息
     *
     * @param paymentAppDTO 公对公申请单DTO
     */
    void updatePaymentApp(PaymentAppDTO paymentAppDTO);

    /**
     * 获取公对公申请单信息
     */
    SimplePaymentAppDTO getPaymentApp(String Code);

    /**
     * 批量更新公对公申请付款信息
     *
     * @param paymentAppDTOList 公对公申请单列表
     */
    void batchUpdatePaymentApp(List<PaymentAppDTO> paymentAppDTOList);

    /**
     * 验证关联编号是否存在
     *
     * @param relationCodes 关联编号列表
     * @return 是否存在
     */
    boolean validateRelationCodeExists(Collection<String> relationCodes);


    /**
     * 验证客户是否存在
     * @param custCode 客户编号
     * @return 是否存在
     */
    boolean validateCustExists(String custCode);

    /**
     * 批量获取公对公申请单信息
     *
     * @param codes 公对公申请单编号列表
     * @return 公对公申请单信息
     */
    Map<String, PaymentAppDTO> getSimplePaymentMsgByCodes(Collection<String> codes);
}
