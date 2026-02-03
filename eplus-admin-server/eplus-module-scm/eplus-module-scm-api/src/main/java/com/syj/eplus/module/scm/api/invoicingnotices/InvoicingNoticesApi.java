package com.syj.eplus.module.scm.api.invoicingnotices;

import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.module.scm.api.invoicingnotices.dto.InvoicingNoticesDTO;
import com.syj.eplus.module.scm.api.invoicingnotices.dto.InvoicingNoticesItemDTO;

import java.util.List;
import java.util.Map;

public interface InvoicingNoticesApi {

    /**
     * 批量生成出票通知
     *
     * @param invoicingNoticesDTOList 请求体
     */
    List<CreatedResponse> createInvoicingNotices(List<InvoicingNoticesDTO> invoicingNoticesDTOList);

    /**
     * 根据出运明细唯一编号查询开票通知明细
     *
     * @param sourceUniqueCodes 出运明细唯一编号列表
     * @return 开票通知明细Map，key为sourceUniqueCode
     */
    Map<String, InvoicingNoticesItemDTO> getInvoicingNoticesItemBySourceUniqueCodes(List<String> sourceUniqueCodes);

    /**
     * 根据采购合同明细ID查询开票通知明细
     *
     * @param purchaseContractItemIds 采购合同明细ID列表
     * @return 开票通知明细Map，key为purchaseContractItemId
     */
    Map<Long, List<InvoicingNoticesItemDTO>> getInvoicingNoticesItemByPurchaseItemIds(List<Long> purchaseContractItemIds);
}
