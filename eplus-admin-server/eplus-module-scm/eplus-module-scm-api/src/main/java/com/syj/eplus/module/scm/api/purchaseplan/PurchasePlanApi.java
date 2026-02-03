package com.syj.eplus.module.scm.api.purchaseplan;

import com.syj.eplus.framework.common.entity.CreatedResponse;
import com.syj.eplus.module.scm.api.purchaseplan.dto.PurchasePlanInfoDTO;
import com.syj.eplus.module.scm.api.purchaseplan.dto.PurchasePlanInfoSaveReqDTO;
import com.syj.eplus.module.scm.api.purchaseplan.dto.PurchasePlanItemDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;


public interface PurchasePlanApi {


    List<CreatedResponse> createPurchasePlan(PurchasePlanInfoSaveReqDTO saveReqVO);

    /**
     * 根据销售合同id获取采购计划数量
     * @param contractId 销售合同id
     * @return 采购计划数量
     */
    Long getOrderNumBySaleContractId(Long contractId);

    /**
     * 查询采购计划详情
     * @param purchasePlanId
     * @return
     */
    PurchasePlanInfoDTO getPurchasePlan(Long purchasePlanId);

    /**
     * 根据外销合同号+产品编码查询采购计划明细
     * @param saleContractCode
     * @param skuCode
     * @return
     */
    PurchasePlanItemDTO getPurchasePlanItemInfo(String saleContractCode, String skuCode,Long companyId);

    List <PurchasePlanItemDTO> getPurchasePlanItemListBySaleContractCodeList(List<String> codeList);

    /**
     * 根据销售明细id列表获取采购明细作废标识
     * @param saleItemIds 销售明细id列表
     */
    Map<Long, List<Integer>> getPurchaseContractItemCancelFlag(List<Long> saleItemIds);

    /**
     * 更新采购计划订单流编号
     * @param linkCodeMap 订单流编号列表
     */
    void updateLinkCodeList(Map<String,String> linkCodeMap);

    /**
     * 根据销售明细id列表获取采购计划模型
     * @param saleItems 销售明细id列表
     */
    Map<Long,Integer> getPurchaseModelBySaleItemIds(Collection<Long> saleItems);
}
