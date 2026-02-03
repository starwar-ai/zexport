package com.syj.eplus.module.fms.api.payment.api.receipt;

import com.syj.eplus.module.fms.api.payment.api.receipt.dto.ReceiptCreateDTO;

/**
 * @Description：财务收款单提供其他模块api
 * @Author：du
 * @Date：2024/4/19 16:16
 */
public interface ReceiptApi {

    void createReceipt(ReceiptCreateDTO receiptCreateDTO);

    /**
     * 根据业务编号获取收款单数量
     * @param businessType 业务类型
     * @param businessCode 业务编号
     * @return 收款单数量
     */
    Long getOrderNumByBusinessCode(Integer businessType,String businessCode);
}
