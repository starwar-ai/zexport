package com.syj.eplus.module.oa.api;

import com.syj.eplus.framework.common.entity.PaymentAppDTO;
import com.syj.eplus.module.oa.api.dto.ReimbDetailDTO;

import java.util.List;

/**
 * @Description：收付款单回写报销相关支付信息
 * @Author：du
 * @Date：2024/4/28 9:40
 */
public interface ReimbApi {
    void updatePaymentStatus(PaymentAppDTO paymentAppDTO);

    /**
     * 批量更新支付状态
     *
     * @param paymentAppDTOList 支付信息
     */
    void batchUpdatePaymentStatus(List<PaymentAppDTO> paymentAppDTOList);

    /**
     * 创建报销明细
     * @param reimbDetailDTO 报销明细
     */
    void createReimbDetail(ReimbDetailDTO reimbDetailDTO);

    /**
     * 获取报销明细
     * @return 报销明细
     */
    List<ReimbDetailDTO> getReimbDetail();


}
