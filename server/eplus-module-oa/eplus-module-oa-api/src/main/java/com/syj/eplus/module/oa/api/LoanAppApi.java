package com.syj.eplus.module.oa.api;

import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.PaymentAppDTO;
import com.syj.eplus.module.oa.api.dto.SimpleLoanAppRespDTO;

import java.util.List;

public interface LoanAppApi {

    /**
     * 根据id列表获取借款单精简列表
     *
     * @param idList id列表
     * @return 借款单精简列表
     */
    List<SimpleLoanAppRespDTO> getSimpleLoanAppRespDTOList(List<Long> idList, Long travelReimbId, Long repayAppId);

    /**
     * @param id
     * @return
     */
    SimpleLoanAppRespDTO getSimpleLoanAppRespDTOById(Long id);

    /**
     * 根据借款单编号获取借款单id列表(模糊查询)
     *
     * @param code 借款单编号
     * @return 借款单id列表
     */
    List<Long> getIdListByCode(String code);

    /**
     * 更新借款单支付信息
     *
     * @param paymentAppDTO
     */
    void updatePaymentStatus(PaymentAppDTO paymentAppDTO);

    /**
     * 更新借款单中已还金额及剩余未还款金额
     *
     * @param repayAmount 本次还款金额
     * @param id          主键
     */
    void updateRepayAmount(JsonAmount repayAmount, Long id);


    /**
     * 批量更新借款单支付信息
     *
     * @param paymentAppDTOList 借款单支付信息列表
     */
    void batchUpdaPaymentStatus(List<PaymentAppDTO> paymentAppDTOList);
}
