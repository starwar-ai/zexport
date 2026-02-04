package com.syj.eplus.module.scm.api.purchasecontract;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.ConfirmSourceEntity;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.framework.common.entity.*;
import com.syj.eplus.module.scm.api.AuxiliaryPurchaseContract.dto.AuxiliaryContractItemDTO;
import com.syj.eplus.module.scm.api.purchasecontract.dto.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PurchaseContractApi {


    CommonResult<PageResult<PurchaseContractSimpleDTO>> getSimplePage(@Valid PurchaseContractGetSimplePageReqDTO pageReqVO);

    PageResult<PurchaseContractItemDTO> getAuxiliarySkuPurchasePage(PurchaseContractGetItemPageReqDTO reqDTO);

    Map<Long, PurchaseContractSimpleDTO> getContractMap(List<Long> idList);

    /**
     * 根据采购合同编号批量获取跟单员缓存
     *
     * @param codeList 采购合同编号列表
     * @return 采购合同跟单员缓存
     */
    Map<String, UserDept> getPurManagerMapByCodeList(List<String> codeList);

    /**
     * 校验采购合同是否存在
     *
     * @param codeList 采购合同编号列表
     * @return 不存在的采购合同编号列表
     */
    Collection<String> validatePurContractExists(Collection<String> codeList);

    /**
     * 根据销售合同编号列表批量获取采购合同精简缓存
     *
     * @param codeList 销售合同编号列表
     * @return 采购合同精简缓存
     */
//    Map<String, PurchaseContractDTO> getPurchaseContractMap(List<String> codeList,Long companyId);

    Map<String, PurchaseContractAllDTO> getPurchaseByCodeList(List<String> codeList);

    /**
     * 根据合同编号查询合同信息
     * @param code
     * @return
     */
    PurchaseContractAllDTO getPurchaseContractByCode(String code);

//    CommonResult<PageResult<PurchaseContractDTO>> getPurchaseContractItemPage(@Valid PurchaseContractGetItemPageReqDTO pageReqVO);

    /**
     * 生成采购合同
     * @param savePurchaseContractReqVO
     * @return
     */
    List<CreatedResponse> genPurchaseContract(SavePurchaseContractReqVO savePurchaseContractReqVO);

    /**
     * 根据供应商编号获取未完成的采购合同列表
     *
     * @param venderCode 供应商编号
     * @return 采购合同列表
     */
    List<PurchaseContractAllDTO> getUnCompletedPurchaseContractByVenderCode(String venderCode);

    /**
     * 根据供应商编号获取未完成的采购合同列表
     *
     * @param skuCode 产品编号
     * @return 采购合同列表
     */
    List<PurchaseContractAllDTO> getUnCompletedPurchaseContractBySkuCode(String skuCode);

    /**
     * 更新采购合同信息的确认状态
     *
     * @param confirmFlag 确认状态
     * @param id 采购合同id
     * @return
     */
    void updateConfirmFlag(Integer confirmFlag, Long id);

    void updateConfirmFlag(Integer confirmFlag, String code);

    /**
     * 供应商变更后更新后续未完成的合同的供应商id
     * @param venderId
     * @param venderCode
     */
    void updateVenderId(Long venderId, String venderCode);

    /**
     * 获得采购合同变更
     *
     * @param targetCode 影响范围主键
     * @return 采购合同
     */
    List<ConfirmSourceEntity> getConfirmSourceList(String targetCode, Integer effectRangeType);

    /**
     * 更新采购合同明细验货状态
     * @param purchaseContractCode
     * @param contractItemMap
     * @return
     */
    Boolean modifyContractItemStatus(String purchaseContractCode, Map<Long, Integer> contractItemMap, LocalDateTime inspectionTime);

    /**
     * 根据销售合同id获取采购合同数量
     * @param auxiliaryFlag 辅料标识
     * @param id 销售合同id
     * @return 采购合同数量
     */
    Long getOrderNumBySaleContractId(boolean auxiliaryFlag, Long id);

    /**
     * 根据销售合同编号查询辅料采购合同中分摊到该销售合同的数据
     * @param code
     * @return
     */
    List<AuxiliaryContractItemDTO> getAuxiliaryContractItemListBySaleCode(String code);

    PurchaseContractItemDTO getItemDetailByItemId(Long auxiliaryPurchaseContractItemId);

    /**
     * 批量更新采购合同入库状态
     * @param billSimpleDTOList 入库列表
     */
    void batchUpdateBillStatus(List<PurchaseBillSimpleDTO> billSimpleDTOList);

    /**
     * 更新采购合同付款计划信息
     * @param saleContractItemIdList 销售明细id
     */
    void updateShipmentDate(List<Long> saleContractItemIdList,List<Integer> dateTypes,LocalDateTime estDepartureTime);


    /**
     * 更新采购合同明细入库状态
     * @param saleContractId
     * @param purchaseContractId
     * @param purchaseItemUniqueCodeList
     * @param purchaseItemBillStatus
     * @return
     */
    Boolean updatePurchaseItem(Long  saleContractId, Long  purchaseContractId, List<String> purchaseItemUniqueCodeList, Integer purchaseItemBillStatus, Map<String,Integer> wmsBillMap,Map<String, Integer> diffBillQuantity);

    /**
     * 更新采购合同明细出运标识
     * @param stockMap
     * @return
     */
    void updatePurchaseItemOutQuantity(Map<String, Map<String, Integer>> stockMap);

    Long getPurchaseItemIdBySaleContractItemId(Long saleContractItemId);

    Map<Long,Long> getPurchaseItemIdBySaleContractItemIds(List<Long> idList);

    /**
     * 根据销售明细唯一标识获取采购价缓存
     * @param saleItemSkuMap 销售明细主键
     * @return 采购价
     */
    Map<Long, PriceQuantityEntity> getPurchaseContractItemPriceMap(Map<Long, String> saleItemSkuMap,Long companyId, String saleContractCode);

    void updatePurchaseAmount(String businessCode, BigDecimal amount);

    Map<Long, Integer> getPurchaseQuantityBySaleContractItemIds(List<Long> saleItemIdList);

    Map<Long, PurchaseContractItemDTO> getPurchaseContractMap(List<Long> saleItemIdList, Map<Long,Long> lastCompanyPathMap );

    /**
     * 通过销售明细id跟主体获取对应采购信息
     * @param saleItemIdList 销售明细id
     * @param companyId 公司id
     * @return  采购合同信息
     */
    Map<Long,String> getPurchaseContractCodeBySaleItemIdList(List<Long> saleItemIdList,Long companyId);

    void updateCheckStatus(String purchaseContractCode, Integer value);

    boolean completeOrderTask(CompleteOrderReqDTO completeOrderReq);

    /**
     * 校验销售明细是否存在对应采购明细
     *
     * @param saleContractItemIds 销售合同明细id列表
     * @return 是否存在对应采购明细
     */
    Collection<Long> validatePurContractItemExists(Collection<Long> saleContractItemIds);

    /**
     * 根据销售合同号获取未完成的采购合同
     *
     * @param saleContractCode 销售合同号
     * @return 出运明细列表
     */
    List<PurchaseContractDTO> getUnCompletedDTOBySaleContractCode(String saleContractCode);

    /**
     * 根据销售明细id列表获取采购明细采购数量缓存
     * @param saleItemIds 销售明细id列表
     * @return 采购明细采购数量缓存
     */
    Map<Long,Map<String,Integer>> getItemQuantityMapBySaleItemIds(List<Long> saleItemIds);

    /**
     * 根据销售明细id列表获取采购明细作废标识
     * @param saleItemIds 销售明细id列表
     */
    Map<Long, List<Integer>> getPurchaseContractItemCancelFlag(List<Long> saleItemIds);

    /**
     * 作废采购计划明细
     * @param saleItemIdList 销售明细id
     */

    void cancelPurchasePlanItem(List<Long> saleItemIdList,Collection<Long> saleItemIds);

    /**
     * 获取影响采购单号
     * @param saleContractCode 销售合同编号
     * @param itemIdList 销售明细id列表
     * @return 采购单号
     */
    List<String> getEffectPurchaseCode(String saleContractCode, List<Long> itemIdList);

    /**
     * 更新采购合同明细开票数量
     * @param invoiceQuantityDTO 开票数量
     */
    void updateInvoiceData(List<InvoiceQuantityDTO> invoiceQuantityDTO);

    /**
     * 获取销售合同明细验货状态
     * @return 验货状态缓存
     */
    Map<Long, Map<String, Integer>> getCheckStatusMap(Set<Long> saleContractItemIds);

    /**
     * 根据销售合同明细id获取采购合同明细id
     * @param saleContractItemIds 销售合同明细id
     * @return 采购明细id
     */
    List<Long> getItemIdsBySaleItems(Long saleContractItemIds);


    /**
     * 根据采购合同编号获取采购合同明细缓存
     * @param purchaseContractCodeList 采购合同编号列表
     * @return 采购合同明细缓存
     */
    Map<String,Map<String,PurchaseContractItemDTO>> getPurchaseContractItemMap(List<String> purchaseContractCodeList);

    /**
     * 删除内部采购合同
     * @param genContractUniqueCode 内部生成唯一编码
     */
    void deleteGenContract(String genContractUniqueCode);

    /**
     * 通过销售明细id获取拆分采购计划编号
     * @param saleContractItemId 销售明细id
     * @return 拆分采购计划编号
     */
    Long getPlanIdBySaleItemId(Long saleContractItemId);

    /**
     * 根据采购合同编号获取供应商编号缓存
     * @return 供应商编号缓存
     */
    Map<String,String> getVenderCodeMapByPurchaseContractCodeList(Collection<String> purchaseContractCodeList);

    /**
     * 批量获取采购合同下单主体缓存
     * @param purchaseContractCodes 采购合同编号列表
     * @return 下单主体缓存
     */
    Map<String, Long> getPurchaseCompanyMap(Collection<String> purchaseContractCodes);

    /**
     * 根据销售明细id获取采购合同编号
     * @param saleItemIds 销售明细id
     * @return 采购合同编号
     */
    Map<Long,Map<String,List<SimpleContractDetailDTO>>> getContractCodeMapByItemId(Collection<Long> saleItemIds);

    /**
     * 修改验货信息
     * @param updateInspectionDTOMap 验货信息
     */
    void updatePurchaseInspectionData(Map<Long,UpdateInspectionDTO> updateInspectionDTOMap);

    /**
     * 获取采购合同明细缓存
     * @param saleColeList 销售编号列表
     * @return 采购合同明细缓存
     */
    Map<Long,List<SimplePurchaseContractItemDTO>> getSimplePurchaseContractItemMap(List<String> saleColeList);

    void updateAuxiliaryPaymentStatusByCodes(List<String> relationCode, Integer value);

    void updateAuxiliaryPurchaseAmount(String pay, JsonAmount amount);

    /**
     * 通过销售合同编号获取采购员列表
     * @param saleContractCodeSet 销售合同编号列表
     * @return 采购员列表
     */
    Map<String,List<UserDept>> getPurchaseUserListBySaleContractCodeSet(Set<String> saleContractCodeSet);

    /**
     * 过滤已转开票合同号
     * @param purchaseCodeList 采购合同列表
     * @return 已转开票的合同列表
     */
    Set<String> filterInvoicedPurchaseContractCode(Collection<String> purchaseCodeList);

    /**
     * 更新采购合同订单流编号
     * @param linkCodeMap 订单流编号列表
     */
    void updateLinkCodeList(Map<String,String> linkCodeMap);

    Map<Long, PurchaseContractItemDTO> getItemMapByItemIds(List<Long> purchaseItemIds);

    /**
     * 获取采购合同单价缓存
     * @param purchaseCodeList 采购合同列表
     * @return 采购合同单价缓存
     */
    Map<Long,JsonAmount> getiPurchasePricCache(Collection<String> purchaseCodeList);


    /**
     * 采购合同回滚
     * @param updateMap 回滚数据
     */
    void rollBackPurchaseNoticeQuantity(Map<String, Map<String, Integer>> updateMap);
}
