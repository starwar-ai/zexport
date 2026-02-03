package com.syj.eplus.module.scm.api.paymentapply;

import com.syj.eplus.framework.common.entity.PaymentAppDTO;
import com.syj.eplus.module.scm.api.paymentapply.dto.PaymentApplyDTO;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/09/17:06
 * @Description:
 */
public interface PaymentApplyApi {
    /**
     * 批量回写付款信息
     *
     * @param paymentAppDTOList 付款信息
     */
    void batchWriteBackPaymentMsg(List<PaymentAppDTO> paymentAppDTOList);

    PaymentApplyDTO getPaymentApply(String code);

    /**
     * 批量回写水单
     */
    void batchWriteAnnex(List<PaymentAppDTO> paymentAppDTOList);
}
