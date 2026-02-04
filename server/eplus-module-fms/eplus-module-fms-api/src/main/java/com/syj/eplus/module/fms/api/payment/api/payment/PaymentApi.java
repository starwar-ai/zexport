package com.syj.eplus.module.fms.api.payment.api.payment;

import com.syj.eplus.module.fms.api.payment.api.payment.dto.ClosePaymentDTO;
import com.syj.eplus.module.fms.api.payment.api.payment.dto.PaymentDTO;
import com.syj.eplus.module.fms.api.payment.api.payment.dto.PaymentSaveDTO;

import java.util.List;
import java.util.Map;

public interface PaymentApi {
    void createPayment(PaymentSaveDTO paymentSaveDTO);

    /**
     * 批量创建付款单
     *
     * @param paymentSaveDTOList 付款单列表
     */
    void batchCreatePayment(List<PaymentSaveDTO> paymentSaveDTOList);

    /**
     * 通过采购合同编号获取付款单数量
     * @param code 采购合同编号
     * @return 付款单数量
     */
    Long getPaymentNumByPurchaseContractCode(Integer businessType,String code);

    /**
     * 校验付款单
     * @param businessType 业务类型
     * @param businessCode 业务编号
     */
    void validatePayment(Integer businessType,String businessCode);

    Map<String,PaymentDTO> getListByCodes(List<String> applyCodes);

    /**
     * 作废付款单
     * @param closePaymentDTO dto
     */
    void closePayment(ClosePaymentDTO closePaymentDTO);
}
