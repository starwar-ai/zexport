package com.syj.eplus.module.sms.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Tuple;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.ConfirmSourceEntity;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.syj.eplus.framework.common.entity.*;
import com.syj.eplus.framework.common.enums.SaleContractStatusEnum;
import com.syj.eplus.module.dms.enums.api.ShipmentApi;
import com.syj.eplus.module.sms.api.dto.*;
import com.syj.eplus.module.sms.controller.admin.salecontract.vo.*;
import com.syj.eplus.module.sms.dal.dataobject.collectionplan.CollectionPlan;
import com.syj.eplus.module.sms.dal.dataobject.salecontract.SaleContractDO;
import com.syj.eplus.module.sms.dal.dataobject.salecontractitem.SaleContractItem;
import com.syj.eplus.module.sms.dal.mysql.collectionplan.CollectionPlanMapper;
import com.syj.eplus.module.sms.dal.mysql.salecontractitem.SaleContractItemMapper;
import com.syj.eplus.module.sms.service.salecontract.SaleContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/15 14:41
 */
@Service
public class SaleContractApiImpl implements SaleContractApi {
    @Resource
    private SaleContractService service;

    @Resource
    private SaleContractItemMapper saleContractItemMapper;

    @Resource
    private ShipmentApi shipmentApi;
    @Autowired
    private CollectionPlanMapper collectionPlanMapper;

    @Override
    public SaleContractDTO getSaleContractById(Long id) {
        SaleContractDO contractDO = service.getById(id);
        if(Objects.isNull(contractDO)){
            return null;
        }
        List<SaleContractItem> contractItems = saleContractItemMapper.selectList(SaleContractItem::getContractId, id);
        if (CollUtil.isEmpty(contractItems)) {
         return null;
        }
        SaleContractDTO saleContractDTO = BeanUtils.toBean(contractDO, SaleContractDTO.class);
        List<SaleContractItemDTO> saleContractItemDTOS = BeanUtils.toBean(contractItems, SaleContractItemDTO.class);
        saleContractDTO.setChildren(saleContractItemDTOS);
        return saleContractDTO;
    }

//    @Override
//    public List<SmsContractAllDTO> getUnCompletedSaleContractByCustCode(String custCode) {
//        Integer[] statusList = {
//                SaleContractStatusEnum.WAITING_FOR_SUBMISSION.getValue(),
//                SaleContractStatusEnum.WAITING_FOR_APPROVAL.getValue(),
//                SaleContractStatusEnum.WAITING_FOR_COUNTERSIGNATURE.getValue(),
//                SaleContractStatusEnum.REJECTED.getValue(),
//                SaleContractStatusEnum.WAITING_FOR_PROCUREMENT.getValue(),
//                SaleContractStatusEnum.WAITING_FOR_SHIPMENT.getValue()
//        };
//        SaleContractPageReqVO saleContractPageReqVO = new SaleContractPageReqVO();
//        saleContractPageReqVO.setPageNo(0).setPageSize(-1);
//        saleContractPageReqVO.setStatusList(statusList);
//        PageResult<SaleContractRespVO> saleContractRespVOPageResult = service.getSaleContractPage(saleContractPageReqVO);
//        if (saleContractRespVOPageResult == null) {
//            return null;
//        }
//        List<SaleContractRespVO> doList = saleContractRespVOPageResult.getList();
//        List<SmsContractAllDTO> dtoList = BeanUtils.toBean(doList, SmsContractAllDTO.class);
//        return dtoList;
//    }

    @Override
    public List<SmsContractAllDTO> getUnCompletedSaleContractByCustCode(String custCode) {
        List<Long> saleContractIds = service.getSaleContractIdsByCustCode(custCode);
        if (CollUtil.isEmpty(saleContractIds)) {
            return null;
        }
        return getUnCompletedSaleContractDetail(saleContractIds);
    }

    public List<SmsContractAllDTO> getUnCompletedSaleContractDetail(List<Long> saleContractIds) {
        Integer[] statusList = {
                SaleContractStatusEnum.WAITING_FOR_SUBMISSION.getValue(),
                SaleContractStatusEnum.WAITING_FOR_APPROVAL.getValue(),
                SaleContractStatusEnum.WAITING_FOR_COUNTERSIGNATURE.getValue(),
                SaleContractStatusEnum.REJECTED.getValue(),
                SaleContractStatusEnum.WAITING_FOR_PROCUREMENT.getValue(),
                SaleContractStatusEnum.WAITING_FOR_SHIPMENT.getValue()
        };
        SaleContractPageReqVO saleContractPageReqVO = new SaleContractPageReqVO();
        saleContractPageReqVO.setPageNo(0).setPageSize(-1);
        saleContractPageReqVO.setIdList(saleContractIds.stream().toArray(Long[]::new));
        saleContractPageReqVO.setStatusList(statusList);
        PageResult<SaleContractRespVO> saleContractRespVOPageResult = service.getSaleContractPage(saleContractPageReqVO, false);
        if (saleContractRespVOPageResult == null) {
            return null;
        }
        List<SaleContractRespVO> doList = saleContractRespVOPageResult.getList();
        List<SmsContractAllDTO> dtoList = BeanUtils.toBean(doList, SmsContractAllDTO.class);
        return dtoList;
    }

    @Override
    public List<SmsContractAllDTO> getUnCompletedSaleContractBySkuCode(String skuCode) {
        List<Long> saleContractIds = service.getSaleContractIdsBySkuCode(skuCode);
        if (CollUtil.isEmpty(saleContractIds)) {
            return null;
        }
        return getUnCompletedSaleContractDetail(saleContractIds);
    }

    @Override
    public List<AddSubItemDTO> getAddSubItemListByContractCodeList(List<String> saleContractCodeList) {
        return service.getAddSubItemListByContractCodeList(saleContractCodeList);
    }

    @Override
    public void batchUpdateAddSubItem(List<AddSubItemDTO> addSubItemDTOList) {
        service.batchUpdateAddSubItem(addSubItemDTOList);
    }

    @Override
    public void updateShipmentQuantity(Map<Long, Integer> shipmentQuantityMap, boolean cancelFlag) {
        service.updateShipmentQuantity(shipmentQuantityMap, cancelFlag);
    }

    @Override
    @DataPermission(enable = false)
    public void updateConfirmFlag(Integer confirmFlag, Long id) {
        service.updateConfirmFlag(confirmFlag, id);
    }

    @Override
    @DataPermission(enable = false)
    public void updateConfirmFlag(Integer confirmFlag, String code) {
        service.updateConfirmFlag(confirmFlag, code);
    }

    @Override
    public void updateCust(Long custId, String custName, String custCode) {
        service.updateCust(custId, custName, custCode);
    }

    @Override
    public List<ConfirmSourceEntity> getConfirmSourceList(String targetCode, Integer effectRangeType) {
        return service.getConfirmSourceList(targetCode, effectRangeType);
    }

    @Override
    public void updateChangeConfirmFlag(Integer effectRangeType, String code) {
        service.updateChangeConfirmFlag(effectRangeType, code);
    }

    @Override
    public Map<String, JsonAmount> generateContract(Long contractId, Long planId, String planCode, Map<Long, Tuple> realPurchasePriceMap,List<String> itemCodeList) {
        // 生成合同
//        return service.generateContract(contractId, planId, planCode, realPurchasePriceMap,itemCodeList);
        return new HashMap<>();
    }

    @Override
    public Boolean syncSaleItemLockInfo(Map<Long, List<StockLockSaveReqVO>> skuLockInfoMap) {
        if (CollUtil.isEmpty(skuLockInfoMap)){
            return false;
        }
        List<SaleContractItem> updateList = new ArrayList<>();
        Set<Long> saleItemIds = skuLockInfoMap.keySet();
        List<SaleContractItem> saleContractItems = saleContractItemMapper.selectBatchIds(saleItemIds);
        if (CollUtil.isEmpty(saleContractItems)){
            return false;
        }
        Map<Long, SaleContractItem> saleContractItemMap = saleContractItems.stream().collect(Collectors.toMap(SaleContractItem::getId, s -> s, (o, n) -> n));
        skuLockInfoMap.forEach((id, list) -> {
            if (CollUtil.isEmpty(list)||CollUtil.isEmpty(saleContractItemMap)){
                return;
            }
            Integer lockQuantity = list.stream().map(StockLockSaveReqVO::getLockQuantity).reduce(Integer::sum).orElse(0);
            SaleContractItem saleContractItem = saleContractItemMap.get(id);
            saleContractItem.setId(id);
            saleContractItem.setRealLockQuantity(lockQuantity);
            saleContractItem.setNeedPurQuantity(saleContractItem.getQuantity() - lockQuantity);
            saleContractItem.setStockLockSaveReqVOList(list);
            updateList.add(saleContractItem);
        });
        if (CollUtil.isNotEmpty(updateList)){
            return saleContractItemMapper.updateBatch(updateList);
        }
        return false;
    }

    @Override
    public List<CreatedResponse> generateSaleContract(SaleContractSaveDTO contractSaveDTO) {
        SaleContractSaveReqVO createReqVO = BeanUtils.toBean(contractSaveDTO, SaleContractSaveReqVO.class);
        List<SaleContractItem> contractItemList = BeanUtils.toBean(contractSaveDTO.getChildren(), SaleContractItem.class);
        createReqVO.setChildren(contractItemList);
        return service.createSaleContract(createReqVO);
    }

    @Override
    public List<HistoryTradePriceDTO> getHistoryTradeList(String code) {

        List<HistoryTradePriceResp> list = service.getHistoryTradePriceBySkuCode(new HistoryTradePriceReq().setSkuCode(code));
        return BeanUtils.toBean(list, HistoryTradePriceDTO.class);

    }

    @Override
    public void batchUpdateBillStatus(List<SaleContractBillSimpleDTO> billSimpleDTOList) {

    }

    @Override
    public Boolean updateSaleItemBillStatus(Long saleContractId, List<String> saleItemUniqueCode,Integer saleItemBillStatus) {
        LambdaQueryWrapper<SaleContractItem> queryWrapper = new LambdaQueryWrapper<>();
        if(ObjectUtil.isNotNull(saleContractId)){
            queryWrapper.eq(SaleContractItem::getContractId, saleContractId);
        }
        if(!CollectionUtils.isEmpty(saleItemUniqueCode)){
            queryWrapper.in(SaleContractItem::getUniqueCode, saleItemUniqueCode);
        }
        List<SaleContractItem> saleContractItemList = saleContractItemMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(saleContractItemList)) {
            List<Long> saleContractItemIdList = saleContractItemList.stream().map(SaleContractItem::getId).distinct().toList();
            shipmentApi.updateSaleItemBillStatus(saleContractItemIdList,saleItemBillStatus);
            saleContractItemList.forEach(x->x.setBillStatus(saleItemBillStatus));
            return saleContractItemMapper.updateBatch(saleContractItemList);
        }
        return false;
    }

    @Override
    @DataPermission(enable = false)
    public SaleContractSaveDTO getSaleContractInfo(Long saleContractId) {
        SaleContractRespVO saleContractRespVO = service.getSaleContract(new SaleContractDetailReq().setSaleContractId(saleContractId), null);
        SaleContractSaveDTO saleContractSaveDTO = BeanUtils.toBean(saleContractRespVO, SaleContractSaveDTO.class);
        List<SaleContractItemSaveDTO> itemSaveDTOS = BeanUtils.toBean(saleContractRespVO.getChildren(), SaleContractItemSaveDTO.class);
        saleContractSaveDTO.setChildren(itemSaveDTOS);
        return saleContractSaveDTO;
    }

    @Override
    public List<JsonCompanyPath> getCompanyPathBySaleContractCodeList(List<String> saleContractCode) {
        return service.getCompanyPathBySaleContractCodeList(saleContractCode);
    }

    private JsonCompanyPath getLastNode(JsonCompanyPath companyPath){
        return Objects.isNull(companyPath.getNext())?companyPath: getLastNode(companyPath.getNext());
    }

    @Override
    public SimpleData getSimpleSaleContractData(String sourceSaleContractCode, Long conpanyId) {
        if (StrUtil.isEmpty(sourceSaleContractCode) || conpanyId == null) {
            return null;
        }
        return service.getSimpleSaleContractData(sourceSaleContractCode, conpanyId);
    }

    @Override
    public Map<String, SimpleData> getBatchSimpleSaleContractData(List<String> contractCodeList, Long conpanyId,Boolean isSale) {
        return service.getBatchSimpleSaleContractData(contractCodeList, conpanyId, isSale);
    }

    @Override
    public void updateItemWithTaxPriceAndManager(Map<String, JsonAmount> withTaxPriceMap, UserDept manager) {
        service.updateItemWithTaxPriceAndManager(withTaxPriceMap,manager);
    }

    @Override
    public Map<Long, Tuple> calcRealPurchasePrice(List<Long> saleItems) {
        return service.calcRealPurchasePrice(saleItems);
    }

    @Override
    public void updateRealPurchasePrice(List<Long> saleItems, UserDept manager,Long purchaseUserId) {
        service.updateRealPurchasePrice(saleItems,manager,purchaseUserId);
    }

    @Override
    public SaleContractSaveDTO getSaleContractInfoByCode(String saleContractCode) {
       Long saleContractId = service.getSaleContractIdByCode(saleContractCode);
       if(Objects.isNull(saleContractId)){
           return null;
       }
         return getSaleContractInfo(saleContractId);

    }

    @Override
    public void validateCompanyPath(Set<String> contractCodeList) {
        service.validateCompanyPath(contractCodeList);
    }

    @Override
    public Map<String, List<String>> getLinkCodeMap(List<String> saleContractCodeList) {
        return service.getLinkCodeMap(saleContractCodeList);
    }

    @Override
    public void batchUpdateShipmentTotalQuantity(List<ShipmentQuantityDTO> dtoList) {
        service.batchUpdateShipmentTotalQuantity(dtoList);
    }

    @Override
    public Map<Long, SaleContractItemDTO> getItemListByIds(List<Long> saleIdList) {
        List<SaleContractItem> itemList = service.getItemListByIds(saleIdList);
        if(CollUtil.isEmpty(itemList)){
            return null;
        }
        List<SaleContractItemDTO> dtoList = BeanUtils.toBean(itemList, SaleContractItemDTO.class);
        return dtoList.stream().collect(Collectors.toMap(SaleContractItemDTO::getId,s->s,(s1,s2)->s1));
    }

    @Override
    public void updateSaleItemPurchaseFlag(List<Long> itemIds, Integer purchaseFlag) {
        service.updateSaleItemPurchaseFlag(itemIds, purchaseFlag);
    }

    @Override
    public List<SmsContractAllDTO> getUnCompletedSaleContractBySaleContractCode(String saleContractCode) {
        List<Long> saleContractIdsBySaleContractCode = service.getSaleContractIdsBySaleContractCode(saleContractCode);
        return getUnCompletedSaleContractDetail(saleContractIdsBySaleContractCode);
    }

    @Override
    public void deleteAutoSaleContract(List<String> sourceSaleContractCode) {
        service.deleteAutoSaleContract(sourceSaleContractCode);
    }

    @Override
    public void batchUpdateConvertShipmentFlag(Map<Long,Integer> shippingQuantityMap) {
        service.batchUpdateConvertShipmentFlag(shippingQuantityMap);
    }

    @Override
    public void updateShipmentDate(List<String> smsContractCodeList,List<Integer> dateTypes) {
        service.updateShipmentDate(smsContractCodeList,dateTypes);
    }

    @Override
    public void setReLockFlag(List<Long> saleItemIdList, Boolean reLockFlag) {
        service.setReLockFlag(saleItemIdList,reLockFlag);
    }

    @Override
    public void setShipmentQuantity(Map<Long, Integer> itemQuantityMap) {
        service.setShipmentQuantity(itemQuantityMap);
    }

    @Override
    public void batchUpdateShippedQuantity(Map<Long,Integer> shippedQuantityMap) {
        service.batchUpdateShippedQuantity(shippedQuantityMap);
    }

    @Override
    public void batchUpdateTransformShippedQuantity(Map<Long, Integer> transformShippedQuantityMap) {
        service.batchUpdateTransformShippedQuantity(transformShippedQuantityMap);
    }

    @Override
    public Boolean checkCollectionPlan(String saleContractCode,Boolean isPurchase, BigDecimal needSum) {
        return service.checkCollectionPlan(saleContractCode,isPurchase,needSum);
    }

    @Override
    public Map<String, SaleContractItemDTO> getItemListByUniqueCodes(List<String> saleUniqueCodeList) {

        return service.getItemListByUniqueCodes(saleUniqueCodeList);
    }

    @Override
    public Map<Long, String> getItemCodesByIds(List<Long> itemIds) {
        return service.getItemCodesByIds(itemIds);
    }

    @Override
    public Map<Long, SimpleSaleItemDTO> splitSaleContractItem(Map<Long, Tuple> saleItemQuantity) {
        return service.splitSaleContractItem(saleItemQuantity);
    }

    @Override
    public void updateShipmentedQuantity(List<CloseShipmentDTO> closeReq) {
        service.updateShipmentedQuantity(closeReq);
    }

    @Override
    public void rollbackSaleContractItemSplitList(List<SplitPurchase> splitList) {
        service.rollbackSaleContractItemSplitList(splitList);
    }

    @Override
    public void genInternalContract(Map<Long,Integer> saleItemMap,Map<Long, Integer> shipmentPurchaseMap,String genContractUniqueCode,Map<Long,SimpleShipment> simpleShipmentMap) {
        if (Objects.isNull(saleItemMap)){
            return;
        }
        service.genInternalContract(saleItemMap,shipmentPurchaseMap,genContractUniqueCode,simpleShipmentMap);
    }

    @Override
    public Long getManufactureCompanyIdByItemId(Long saleContractItemId) {
        return service.getManufactureCompanyIdByItemId(saleContractItemId);
    }

    @Override
    public Map<String, List<ShipmentPriceEntity>> getShipmentPriceBySaleContractCode(List<String> saleContractCodeList) {
        return service.getShipmentPriceBySaleContractCode(saleContractCodeList);
    }

    @Override
    public void rollbackSaleContractItemPurchaseQuantity(Map<Long, Integer> purchaseQuantityMap) {
        service.rollbackSaleContractItemPurchaseQuantity(purchaseQuantityMap);
    }

    @Override
    public void reLockStock(List<Long> saleContractItemIdList) {
        service.reLockStock(saleContractItemIdList);
    }

    @Override
    public SaleContractItemDTO getSaleContractItemById(Long saleContractItemId) {
        return service.getSaleContractItemById(saleContractItemId);
    }

    @Override
    public Integer deleteSplitSaleItem(List<Long> saleContractItemIdList) {
        return service.deleteSplitSaleItem(saleContractItemIdList);
    }

    @Override
    public void rollbackSaleContractItemSourceList(List<Long> saleContractItemIdList) {
        service.rollbackSaleContractItemSourceList(saleContractItemIdList);
    }

    @Override
    public void deleteGenContract(String genContractUniqueCode) {
        service.deleteGenContract(genContractUniqueCode);
    }

    @Override
    public Map<Long, String> getSaleContractItemIdMap(List<Long> saleContractItemIdList) {
        if (CollUtil.isEmpty(saleContractItemIdList)){
            return Map.of();
        }
        return service.getSaleContractItemIdMap(saleContractItemIdList);
    }

    @Override
    public Map<Long, String> getSaleItemUniqueCodeMap(List<Long> saleContractItemIdList) {
        if (CollUtil.isEmpty(saleContractItemIdList)){
            return Map.of();
        }
        List<SaleContractItem> saleContractItems = saleContractItemMapper.selectBatchIds(saleContractItemIdList);
        if (CollUtil.isEmpty(saleContractItems)){
            return Map.of();
        }
        return saleContractItems.stream().collect(Collectors.toMap(SaleContractItem::getId, SaleContractItem::getUniqueCode));
    }

    @Override
    public Map<Long, Integer> getSaleContractItemSplitPurchaseMap(List<Long> saleContractItemIdList) {
        if (CollUtil.isEmpty(saleContractItemIdList)){
            return Map.of();
        }
        List<SaleContractItem> saleContractItems = saleContractItemMapper.selectBatchIds(saleContractItemIdList);
        if (CollUtil.isEmpty(saleContractItems)){
            return Map.of();
        }
        return saleContractItems.stream().collect(Collectors.toMap(SaleContractItem::getId, SaleContractItem::getSplitPurchaseFlag));
    }

    @Override
    public Map<Long, Map<String, JsonAmount>> getCollectionCalimAmountMap(List<Long> collectionIdList) {
        if (CollUtil.isEmpty(collectionIdList)){
            return Map.of();
        }
        List<CollectionPlan> collectionPlans = collectionPlanMapper.selectBatchIds(collectionIdList);
        if (CollUtil.isEmpty(collectionPlans)){
            return Map.of();
        }
        return collectionPlans.stream()
                .filter(plan -> CollUtil.isNotEmpty(plan.getChildren()))
                .collect(Collectors.groupingBy(
                        CollectionPlan::getId,
                        Collectors.flatMapping(
                                plan -> plan.getChildren().stream().filter(item -> Objects.nonNull(item.getInvoiceCode())),
                                Collectors.groupingBy(
                                        CollectionPlanItem::getInvoiceCode,
                                        Collectors.mapping(
                                                CollectionPlanItem::getAmount,
                                                Collectors.reducing(new JsonAmount(), JsonAmount::add)
                                        )
                                )
                        )
                ));
    }

    @Override
    public Map<Long, Integer> getUnTransformShipmentedQuantityMap(List<Long> itemIds) {
       if (CollUtil.isEmpty(itemIds)){
           return Map.of();
       }
        List<SaleContractItem> saleContractItems = saleContractItemMapper.selectBatchIds(itemIds);
       if (CollUtil.isEmpty(saleContractItems)){
           return Map.of();
       }
       return saleContractItems.stream().collect(Collectors.toMap(SaleContractItem::getId, s-> s.getQuantity() - (Objects.isNull(s.getTransferShippedQuantity())?0:s.getTransferShippedQuantity())));
    }

    @Override
    public void rollBackTransformShipmentedQuantityByChange(Map<Long, Integer> updateQuantityMap,boolean isAddFlag) {
        if (CollUtil.isEmpty(updateQuantityMap)){
            return;
        }
        Set<Long> saleItemIds = updateQuantityMap.keySet();
        List<SaleContractItem> saleContractItems = saleContractItemMapper.selectBatchIds(saleItemIds);
        if (CollUtil.isEmpty(saleContractItems)){
            return;
        }
        saleContractItems.forEach(s->{
            Integer updateQuantity = updateQuantityMap.get(s.getId());
            if (isAddFlag){
                // 数量减少时回写（增加待转出运数量）
                s.setTransferShippedQuantity(s.getTransferShippedQuantity() + updateQuantity);
            }else {
                // 数量增加时回写（减少待转出运数量）
                s.setTransferShippedQuantity(s.getTransferShippedQuantity() - updateQuantity);
            }
        });
        // 保存到数据库
        saleContractItemMapper.updateBatch(saleContractItems);
    }

    @Override
    public void genInternalContractByOutBill(Map<String, Integer> saleItemMap,String genContractUniqueCode) {
        service.genInternalContractByOutBill(saleItemMap,genContractUniqueCode);
    }

    @Override
    public Map<String, JsonCompanyPath> getCompanyPathMapBySaleContractCodeList(List<String> saleContractCodes) {
        if (CollUtil.isEmpty(saleContractCodes)){
            return Map.of();
        }
       return service.getCompanyPathMapBySaleContractCodeList(saleContractCodes);
    }

    @Override
    public void batchUpdatePurchaseUser(Set<String> saleContractCodeSet) {
        service.batchUpdatePurchaseUser(saleContractCodeSet);
    }

    @Override
    public void rewritePurchaseFreeQuantity(Map<Long, Integer> freeQUantityMap,boolean isConcel) {
        service.rewritePurchaseFreeQuantity(freeQUantityMap,isConcel);
    }

    @Override
    public Map<String,List<UserDept>> getSalesByContractCodeSet(Set<String> contractCodeList) {
        if (CollUtil.isEmpty(contractCodeList)){
            return Map.of();
        }
        return service.getSalesByContractCodeSet(contractCodeList);
    }

    @Override
    public void updateLinkCodeList(Map<String,String> linkCodeMap) {
        service.updateLinkCodeList(linkCodeMap);
    }

    @Override
    public void updateShipmentTotalQuantity(Map<Long, Integer> shipmentTotalQuantityMap,boolean isDeletedFlag) {
        service.updateShipmentTotalQuantity(shipmentTotalQuantityMap,isDeletedFlag);
    }

    @Override
    public Map<Long, JsonAmount> getSaleContractItemAmountMap(List<Long> saleContractItemIdList) {
        return service.getSaleContractItemAmountMap(saleContractItemIdList);
    }

    @Override
    public Map<Long, Map<String, Integer>> getSalePurchaseAndStockQuantityMap(Collection<Long> itemIds) {
        return service.getSalePurchaseAndStockQuantityMap(itemIds);
    }

    @Override
    public boolean validateCustExists(String custCode) {
        return service.validateCustExists(custCode);
    }

    @Override
    public Map<Long, JsonAmount> getRealPurchasePriceMapByItemIds(Collection<Long> itemIds) {
        return service.getRealPurchasePriceMapByItemIds(itemIds);
    }

    @Override
    public Map<Long, Boolean> getSaleItemSaleType(Collection<Long> itemIds) {
        if (CollUtil.isEmpty(itemIds)){
            return Map.of();
        }
        return service.getSaleItemSaleType(itemIds);
    }

    @Override
    public Map<String, List<Long>> getCompanyIdListBySaleContractCodeList(List<String> saleContractCodeList) {
        return service.getCompanyIdListBySaleContractCodeList(saleContractCodeList);
    }

    @Override
    public void writeBackContract(List<WriteBackDTO> writeBackDTOList,boolean rollbackFlag) {
        service.writeBackContract(writeBackDTOList,rollbackFlag);
    }

    @Override
    @DataPermission(enable = false)
    public Map<String, SmsContractAllDTO> getSmsByCodeList(List<String> codeList) {
        List<SaleContractDO> doList = service.getSmsByCodeList(codeList);
        List<SmsContractAllDTO> dtoList = BeanUtils.toBean(doList, SmsContractAllDTO.class);
        if (CollUtil.isNotEmpty(doList)) {
            return dtoList.stream().collect(Collectors.toMap(SmsContractAllDTO::getCode, s -> s));
        }
        return null;
    }

    @Override
    @DataPermission(enable = false)
    public List<SaleContractItemSaveDTO> listSaleContractItem(List<Long> saleContractItemIdList) {
        List<SaleContractItem> saleContractItemList = saleContractItemMapper.selectBatchIds(saleContractItemIdList);
        return BeanUtils.toBean(saleContractItemList, SaleContractItemSaveDTO.class);
    }
}
