package com.syj.eplus.module.sms.api;

import cn.hutool.core.lang.Tuple;
import cn.iocoder.yudao.framework.common.pojo.ConfirmSourceEntity;
import com.syj.eplus.framework.common.entity.*;
import com.syj.eplus.module.sms.api.dto.*;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/9 10:31
 */
public interface SaleContractApi {

    SaleContractDTO getSaleContractById(Long id);

    /**
     * 根据销售合同编号获取加减项列表
     *
     * @param saleContractCodeList 销售合同编号列表
     * @return 加减项列表
     */
    List<AddSubItemDTO> getAddSubItemListByContractCodeList(List<String> saleContractCodeList);


    /**
     * 批量更新加减项
     *
     * @param addSubItemDTOList 加减项列表
     */
    void batchUpdateAddSubItem(List<AddSubItemDTO> addSubItemDTOList);

    /**
     * 回写出运数量
     *
     * @param shipmentQuantityMap 出运数量map
     */
    void updateShipmentQuantity(Map<Long, Integer> shipmentQuantityMap, boolean cancelFlag);

    /**
     * 认领回写收款计划及加项
     *
     * @param writeBackDTOList 认领明细DTO
     */
    void writeBackContract(List<WriteBackDTO> writeBackDTOList,boolean rollbackFlag);

    Map<String, SmsContractAllDTO> getSmsByCodeList(List<String> codeList);

    /**
     * 根据客户编号获取未完成的销售合同列表
     *
     * @param custCode 客户编号
     * @return 销售合同列表
     */
    List<SmsContractAllDTO> getUnCompletedSaleContractByCustCode(String custCode);

    /**
     * 根据产品编号获取未完成的销售合同列表
     *
     * @param skuCode 产品编号
     * @return 销售合同列表
     */
    List<SmsContractAllDTO> getUnCompletedSaleContractBySkuCode(String skuCode);

    /**
     * 更新销售合同信息的确认状态
     *
     * @param confirmFlag 确认状态
     * @param id          销售合同id
     * @return
     */
    void updateConfirmFlag(Integer confirmFlag, Long id);

    void updateConfirmFlag(Integer confirmFlag, String code);

    /**
     * 客户变更后更新后续未完成的合同的客户id
     * @param custId
     * @param custName
     * @param custCode
     */
    void updateCust(Long custId, String custName, String custCode);

    /**
     * 获得外销合同变更
     *
     * @param targetCode 影响范围主键
     * @return 外销合同
     */
    List<ConfirmSourceEntity> getConfirmSourceList(String targetCode, Integer effectRangeType);


    /**
     * 更新外销合同变更确认状态
     *
     * @param effectRangeType 影响范围类型
     * @param code            影响范围编号
     */
    void updateChangeConfirmFlag(Integer effectRangeType, String code);

    /**
     * 生成订单路径中的购销合同
     * @param contractId
     * @param realPurchasePriceMap
     * @return
     */
    Map<String, JsonAmount> generateContract(Long contractId, Long planId, String planCode, Map<Long, Tuple> realPurchasePriceMap,List<String> itemCodeList);

    /**
     *
     * @param skuLockInfoMap
     * @return
     */
    Boolean syncSaleItemLockInfo(Map<Long, List<StockLockSaveReqVO>> skuLockInfoMap);


    /**
     * 生成销售合同
     * @param contractSaveDTO
     * @return
     */
    List<CreatedResponse> generateSaleContract(SaleContractSaveDTO contractSaveDTO);


    List<HistoryTradePriceDTO> getHistoryTradeList(String code);

    /**
     * 批量更新销售合同入库状态
     * @param billSimpleDTOList 入库列表
     */
    void batchUpdateBillStatus(List<SaleContractBillSimpleDTO> billSimpleDTOList);

    /**
     * 更新外销合同明细入库状态
     * @param saleContractId
     * @param saleItemUniqueCode
     * @param saleItemBillStatus
     * @return
     */
    Boolean updateSaleItemBillStatus(Long saleContractId, List<String> saleItemUniqueCode,Integer saleItemBillStatus);

    /**
     *  查询销售合同信息
     * @param saleContractId
     * @return
     */
    SaleContractSaveDTO getSaleContractInfo(Long saleContractId);

    /**
     * 根据销售合同编号获取订单路径
     * @param saleContractCodes 销售合同编号列表
     * @return 订单路径
     */
    List<JsonCompanyPath> getCompanyPathBySaleContractCodeList(List<String> saleContractCodes);

    /**
     * 根据来源编号获取自动生成销售合同信息
     * @param sourceSaleContractCode 来源编号
     * @param conpanyId 公司主键
     * @return 销售合同信息
     */
    SimpleData getSimpleSaleContractData(String sourceSaleContractCode,Long conpanyId);

    /**
     * 根据来源编号批量获取自动生成销售合同信息
     * @param contractCodeList 来源编号
     * @param conpanyId 公司主键
     * @return 销售合同信息
     */
    Map<String,SimpleData> getBatchSimpleSaleContractData(List<String> contractCodeList,Long conpanyId,Boolean isSale);

    /**
     * 获取销售合同明细详情
     * @param saleContractItemIdList
     * @return
     */
    List<SaleContractItemSaveDTO> listSaleContractItem(List<Long> saleContractItemIdList);

    /**
     * 根据唯一标识批量更新销售合同明细含税价格
     * @param withTaxPriceMap 采购含税价格缓存
     */
    void updateItemWithTaxPriceAndManager(Map<String, JsonAmount> withTaxPriceMap, UserDept manager);

    /**
     * 计算真实采购价
     * @param saleItems 销售明细id
     */
    Map<Long, Tuple> calcRealPurchasePrice(List<Long> saleItems);

    /**
     * 更新销售合同真实采购价
     * @param saleItemIds 销售明细id
     * @param manager 跟单员
     * @param purchaseUserId 真实采购员
     */
    void updateRealPurchasePrice(List<Long> saleItemIds, UserDept manager,Long purchaseUserId);

    SaleContractSaveDTO getSaleContractInfoByCode(String saleContractCode);

    /**
     * 校验公司路径
     * @param contractCodeList 销售合同编号列表
     */
    void validateCompanyPath(Set<String> contractCodeList);

    /**
     * 根据销售合同编号获取销售合同链路编号缓存
     * @param saleContractCodeList 销售合同编号列表
     * @return 销售合同链路编号缓存
     */
    Map<String,List<String>> getLinkCodeMap(List<String> saleContractCodeList);

    void batchUpdateShipmentTotalQuantity(List<ShipmentQuantityDTO> dtoList);

    Map<Long, SaleContractItemDTO> getItemListByIds(List<Long> saleIdList);

    /**
     * 回写销售合同明细的转采购标识
     * @param itemIds 销售合同明细id列表
     * @param purchaseFlag 转采购标识
     */
    void updateSaleItemPurchaseFlag(List<Long> itemIds, Integer purchaseFlag);

    /**
     * 根据客户编号获取未完成的销售合同列表
     *
     * @param saleContractCode 销售合同号
     * @return 销售合同列表
     */
    List<SmsContractAllDTO> getUnCompletedSaleContractBySaleContractCode(String saleContractCode);

    /**
     * 删除自动生成的销售合同
     * @param sourceSaleContractCode 来源销售合同编号
     */
    void deleteAutoSaleContract(List<String> sourceSaleContractCode);

    /**
     * 批量更新销售合同转出运标识
     * @param shippingQuantityMap 出运数量缓存
     */
    void batchUpdateConvertShipmentFlag(Map<Long,Integer> shippingQuantityMap);

    /**
     * 更新销售合同出运日期
     * @param smsContractCodeList 销售合同编号列表
     */
    void updateShipmentDate(List<String> smsContractCodeList,List<Integer> dateTypes);


    /**
     * 根据销售合同编号获取销售合同信息
     * @param saleContractCodeList 销售合同编号列表
     * @return 销售合同信息
     */
    Map<String,List<Long>> getCompanyIdListBySaleContractCodeList(List<String> saleContractCodeList);

    void setReLockFlag(List<Long> saleItemIdList,Boolean reLockFlag);

    void setShipmentQuantity(Map<Long, Integer> itemQuantityMap);

    /**
     * 批量更新销售合同明细已出运数量
     * @param shippedQuantityMap 销售明细出运数量缓存
     */
    void batchUpdateShippedQuantity(Map<Long,Integer> shippedQuantityMap);

    /**
     * 批量更新销售合同明细转出运数量
     */
    void batchUpdateTransformShippedQuantity(Map<Long,Integer> transformShippedQuantityMap);

    Boolean checkCollectionPlan(String saleContractCode,Boolean isPurchase, BigDecimal needSum);

    Map<String, SaleContractItemDTO> getItemListByUniqueCodes(List<String> saleUniqueCodeList);

    Map<Long, String> getItemCodesByIds(List<Long> itemIds);

    /**
     *
     * @param saleItemQuantity
     * @return
     */
    Map<Long,SimpleSaleItemDTO> splitSaleContractItem(Map<Long, Tuple> saleItemQuantity);

    void updateShipmentedQuantity(List<CloseShipmentDTO> closeReq);

    /**
     * 回写销售合同明细拆单信息
     * @param splitList 拆单信息
     */
    void rollbackSaleContractItemSplitList(List<SplitPurchase> splitList);

    /**
     * 生成内部合同
     * @param saleItemMap 销售明细数量缓存     */
    void genInternalContract(Map<Long,Integer> saleItemMap,Map<Long, Integer> shipmentPurchaseMap,String genContractUniqueCode,Map<Long,SimpleShipment> simpleShipmentMap);

    /**
     * 通过销售明细id获取加工主体
     * @param saleContractItemId 销售明细id
     * @return 加工主体
     */
    Long getManufactureCompanyIdByItemId(Long saleContractItemId);

    /**
     * 通过销售合同号获取出运对应的应收金额
     * @param saleContractCodeList 销售合同编号
     * @return 出运对应的应收金额
     */
    Map<String,List<ShipmentPriceEntity>> getShipmentPriceBySaleContractCode(List<String> saleContractCodeList);

    /**
     * 回写真实采购数量
     * @param purchaseQuantityMap 真实采购数量缓存
     */
    void rollbackSaleContractItemPurchaseQuantity(Map<Long,Integer> purchaseQuantityMap);

    /**
     * 重新锁定销售合同库存
     *
     * @param saleContractItemIdList 销售明细主键列表
     */
    void reLockStock(List<Long> saleContractItemIdList);

    /**
     * 获取销售明细信息
     * @param saleContractItemId 销售明细主键
     * @return 销售明细信息
     */
    SaleContractItemDTO getSaleContractItemById(Long saleContractItemId);

    /**
     * 删除拆分销售合同明细
     * @param saleContractItemIdList 销售明细主键列表
     */
    Integer deleteSplitSaleItem(List<Long> saleContractItemIdList);

    /**
     * 回写来源销售合同明细数量及转采购标识
     * @param saleContractItemIdList 销售明细主键列表
     */
    void rollbackSaleContractItemSourceList(List<Long> saleContractItemIdList);

    void deleteGenContract(String genContractUniqueCode);

    /**
     * 根据拆分后销售明细id获取拆分前销售明细id缓存
     * @param saleContractItemIdList 销售明细id列表
     * @return 销售明细id缓存
     */
    Map<Long,String> getSaleContractItemIdMap(List<Long> saleContractItemIdList);

    /**
     * 获取销售明细唯一码缓存
     * @param saleContractItemIdList 销售明细id
     * @return 唯一码缓存
     */
    Map<Long,String> getSaleItemUniqueCodeMap(List<Long> saleContractItemIdList);

    /**
     * 获取销售明细拆分采购标识缓存
     * @param saleContractItemIdList 销售明细id列表
     * @return 拆分采购标识缓存
     */
    Map<Long,Integer> getSaleContractItemSplitPurchaseMap(List<Long> saleContractItemIdList);

    /**
     * 获取收款计划实收金额缓存
     * @param collectionIdList 收款计划id列表
     * @return 实收金额缓存
     */
    Map<Long,Map<String,JsonAmount>> getCollectionCalimAmountMap(List<Long> collectionIdList);

    /**
     * 获取未转换出运数量缓存
     * @param itemIds 销售明细id列表
     * @return 未转换出运数量缓存
     */
    Map<Long,Integer> getUnTransformShipmentedQuantityMap(List<Long> itemIds);

    /**
     * 回写出运数量
     * @param updateQuantityMap 更新缓存
     */
    void rollBackTransformShipmentedQuantityByChange(Map<Long,Integer> updateQuantityMap,boolean isAddFlag);

    /**
     * 出库单提交生成内部合同
     * @param saleItemMap 销售明细缓存
     */
    void genInternalContractByOutBill(Map<String,Integer> saleItemMap,String genContractUniqueCode);

    /**
     * 根据销售合同编号获取订单路径
     * @param saleContractCodes 销售合同编号列表
     * @return 订单路径
     */
    Map<String,JsonCompanyPath> getCompanyPathMapBySaleContractCodeList(List<String> saleContractCodes);

    /**
     * 批量更新销售合同
     * @param saleContractCodeSet 销售合同编号列表
     */
    void batchUpdatePurchaseUser(Set<String> saleContractCodeSet);

    /**
     * 回写采购赠品数量
     * @param freeQUantityMap 采购赠品数量缓存
     */
    void rewritePurchaseFreeQuantity(Map<Long,Integer> freeQUantityMap,boolean isConcel);

    /**
     * 根据销售合同编号获取销售员列表
     * @param contractCodeList 销售合同编号列表
     * @return 销售员列表
     */
    Map<String,List<UserDept>> getSalesByContractCodeSet(Set<String> contractCodeList);


    /**
     * 更新销售合同订单流编号
     * @param linkCodeMap 订单流编号列表
     */
    void updateLinkCodeList(Map<String,String> linkCodeMap);

    /**
     * 更新销售合同转出运数量
     * @param shipmentTotalQuantityMap 出运数量缓存
     */
    void updateShipmentTotalQuantity(Map<Long,Integer> shipmentTotalQuantityMap,boolean isDeletedFlag);

    /**
     * 获取销售合同明细金额缓存
     * @param saleContractItemIdList 销售明细id列表
     * @return 销售合同明细金额缓存
     */
    Map<Long,JsonAmount> getSaleContractItemAmountMap(List<Long> saleContractItemIdList);

    /**
     * 获取销售合同明细采购数量缓存
     * @param itemIds 销售明细id列表
     * @return 销售合同明细采购数量缓存
     */
    Map<Long,Map<String,Integer>> getSalePurchaseAndStockQuantityMap(Collection<Long> itemIds);

    /**
     * 验证客户是否存在
     * @param custCode 客户编号
     * @return 是否存在
     */
    boolean validateCustExists(String custCode);

    /**
     * 获取销售合同明细采购价格缓存
     * @param itemIds 销售明细id列表
     * @return 销售合同明细采购价格缓存
     */
    Map<Long,JsonAmount> getRealPurchasePriceMapByItemIds(Collection<Long> itemIds);

    /**
     * 获取销售合同明细销售类型缓存
     * @param itemIds 销售明细id列表
     * @return 销售合同明细销售类型缓存
     */
    Map<Long,Boolean> getSaleItemSaleType(Collection<Long> itemIds);
}
