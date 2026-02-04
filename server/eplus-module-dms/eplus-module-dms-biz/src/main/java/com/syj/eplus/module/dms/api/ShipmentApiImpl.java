package com.syj.eplus.module.dms.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.syj.eplus.framework.common.dict.CalculationDict;
import com.syj.eplus.framework.common.entity.*;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.framework.common.enums.ShippingStatusEnum;
import com.syj.eplus.framework.common.util.NumUtil;
import com.syj.eplus.module.dms.controller.admin.shipment.vo.ShipmentPageReqVO;
import com.syj.eplus.module.dms.controller.admin.shipment.vo.ShipmentRespVO;
import com.syj.eplus.module.dms.dal.dataobject.shipment.ShipmentDO;
import com.syj.eplus.module.dms.dal.dataobject.shipmentitem.ShipmentItem;
import com.syj.eplus.module.dms.dal.mysql.shipment.ShipmentMapper;
import com.syj.eplus.module.dms.dal.mysql.shipmentitem.ShipmentItemMapper;
import com.syj.eplus.module.dms.enums.api.ShipmentApi;
import com.syj.eplus.module.dms.enums.api.dto.DeclarationItemDTO;
import com.syj.eplus.module.dms.enums.api.dto.ShipmentDTO;
import com.syj.eplus.module.dms.enums.api.dto.ShipmentItemDTO;
import com.syj.eplus.module.dms.enums.api.dto.SimpleShipmentItemDTO;
import com.syj.eplus.module.dms.service.declaration.DeclarationService;
import com.syj.eplus.module.dms.service.forwarderfee.ForwarderFeeService;
import com.syj.eplus.module.dms.service.shipment.ShipmentService;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @Description：
 * @Author：chengbo
 * @Date：2024/8/30 17:38
 */
@Service
public class ShipmentApiImpl implements ShipmentApi {
    @Resource
    @Lazy
    private ShipmentService service;

    @Resource
    @Lazy
    private ForwarderFeeService forwarderFeeService;
    @Resource
    private ShipmentItemMapper shipmentItemMapper;
    @Resource
    private ShipmentMapper shipmentMapper;

    @Resource
    @Lazy
    private DeclarationService declarationService;

    private List<ShipmentDTO> getUnShippedDTODetail(List<Long> shipmentIds) {
        List<Integer> statusList =List.of(
                ShippingStatusEnum.AWAITING_PROCESSING.getValue(),
                ShippingStatusEnum.READY_FOR_SHIPMENT.getValue(),
                ShippingStatusEnum.SHIPPED_IN_BATCH.getValue());
        ShipmentPageReqVO shipmentPageReqVO = new ShipmentPageReqVO();
        shipmentPageReqVO.setPageNo(0).setPageSize(-1);
        shipmentPageReqVO.setIdList(shipmentIds);
        shipmentPageReqVO.setStatusList(statusList);
        PageResult<ShipmentRespVO> shipmentPage = service.getShipmentPage(shipmentPageReqVO);
        if (shipmentPage == null) {
            return null;
        }
        List<ShipmentRespVO> doList = shipmentPage.getList();
        List<ShipmentDTO> dtoList = BeanUtils.toBean(doList, ShipmentDTO.class);
        return dtoList;
    }
    @Override
    public List<ShipmentDTO> getUnShippedDTOByCustCode(String custCode) {
        List<Long> shipmentIds = service.getShipmentIdsByCustCode(custCode);
        if (CollUtil.isEmpty(shipmentIds)) {
            return null;
        }
        return getUnShippedDTODetail(shipmentIds);
    }

    @Override
    public List<ShipmentDTO> getUnShippedDTOByVenderCode(String venderCode) {
        List<Long> shipmentIds = service.getShipmentIdsByCustCode(venderCode);
        if (CollUtil.isEmpty(shipmentIds)) {
            return null;
        }
        return getUnShippedDTODetail(shipmentIds);
    }

    @Override
    public List<ShipmentDTO> getUnShippedDTOBySkuCode(String skuCode) {
        List<Long> shipmentIds = service.getShipmentIdsBySkuCode(skuCode);
        if (CollUtil.isEmpty(shipmentIds)) {
            return null;
        }
        return getUnShippedDTODetail(shipmentIds);
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
    public void updateVender(Long venderId, String venderName, String venderCode) {
        service.updateVender(venderId, venderName, venderCode);
    }

    @Override
    public void updateCust(Long custId, String custName, String custCode) {
        service.updateCust(custId, custName, custCode);
    }

    @Override
    public Long getOrderNumBySaleContractCode(String code) {
        return service.getOrderNumBySaleContractCode(code);
    }

    @Override
    public Long getPlanNumBySaleContractCode(String code) {
        return service.getPlanNumBySaleContractCode(code);
    }

    @Override
    public void updateShipmentItemInvoiceStatus(List<String> uniqueCodeList,Integer status) {
        service.updateShipmentItemInvoiceStatus(uniqueCodeList,status);
    }

    @Override
    public void updateShipmentIItemInvoiceQuantity(Map<Long, BigDecimal> quantityMap) {
        service.updateShipmentIItemInvoiceQuantity(quantityMap);
    }

    @Override
    public Map<String,ShipmentDTO> getByShipmentCodeList(List<String> shipmentCodeList) {

        return service.getByShipmentCodeList(shipmentCodeList);
    }

    @Override
    public ShipmentDTO getByShipmentCode(String shipmentCode) {
        return service.getByShipmentCode(shipmentCode);

    }

    @Override
    public Map<Long, DeclarationItemDTO> getDeclarationListByShipmentId(List<Long> list) {
       return declarationService.getDeclarationListByShipmentId(list);

    }

    @Override
    public List<ShipmentDTO> getUnShippedDTOBySaleContractCode(String saleContractCode) {
        List<Long> shipmentIds = service.getShipmentIdsBySaleContractCode(saleContractCode);
        if (CollUtil.isEmpty(shipmentIds)) {
            return null;
        }
        return getUnShippedDTODetail(shipmentIds);
    }

    @Override
    public void updateOutQuantity(String shipmentCode, Map<Long, Integer> skuQuantityMap, LocalDateTime outDate) {
        service.updateOutQuantity(shipmentCode, skuQuantityMap, outDate);
    }

    @Override
    public void cancelConvertContainerFlag(List<String> uniqueCodeList) {
        service.cancelConvertContainerFlag(uniqueCodeList);
    }

    @Override
    public void updateRealPurchasePrice(String saleContractCode, UserDept manager) {
        service.updateRealPurchasePrice(saleContractCode, manager);
    }

    @Override
    public Map<Long, List<Integer>> getShipmentContractItemCancelFlag(List<Long> itemIdList) {
        if (CollUtil.isEmpty(itemIdList)){
            return Map.of();
        }
        return service.getShipmentContractItemCancelFlag(itemIdList);
    }

    @Override
    public void cancelShipmentPlanItem(List<Long> itemIdList) {
        service.cancelShipmentPlanItem(itemIdList);
    }

    @Override
    public List<String> getEffectShipmentCode(String saleContractCode, List<Long> itemIdList) {
        return service.getEffectShipmentCode(saleContractCode,itemIdList);
    }

    @Override
    public Boolean updateSaleItemBillStatus(List<Long> saleItemList,Integer saleItemBillStatus) {
        LambdaQueryWrapper<ShipmentItem> queryWrapper = new LambdaQueryWrapper<>();
        if(!CollectionUtils.isEmpty(saleItemList)){
            queryWrapper.in(ShipmentItem::getSaleContractItemId, saleItemList);
        }
        List<ShipmentItem> shipmentItemList = shipmentItemMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(shipmentItemList)) {
            shipmentItemList.forEach(x->x.setBillStatus(saleItemBillStatus));
            return shipmentItemMapper.updateBatch(shipmentItemList);
        }
        return false;
    }

    @Override
    public void updateCheckStatus(Map<Long, Map<String, Integer>> updateMap) {
        if (CollUtil.isEmpty(updateMap)){
            return;
        }
        Set<Long> saleItemIds = updateMap.keySet();
        List<ShipmentItem> shipmentItems = shipmentItemMapper.selectList(ShipmentItem::getSaleContractItemId, saleItemIds);
        if (CollUtil.isEmpty(shipmentItems)){
            return;
        }
        List<ShipmentItem> updateList = shipmentItems.stream().map(s -> {
            Map<String, Integer> checkStatusMap = updateMap.get(s.getSaleContractItemId());
            if (CollUtil.isEmpty(checkStatusMap)) {
                return null;
            }
            Integer status = checkStatusMap.get(s.getSkuCode());
            s.setCheckStatus(status);
            return s;
        }).filter(Objects::nonNull).toList();
        shipmentItemMapper.updateBatch(updateList);
    }

    @Override
    public Map<String, List<ShipmentPriceEntity>> getShipmentPriceBySaleContractCode(List<String> saleContractCodeList) {
        Map<String, List<ShipmentPriceEntity>> result = new HashMap<>();
       if (CollUtil.isEmpty(saleContractCodeList)){
           return result;
       }
        List<ShipmentItem> shipmentItems = shipmentItemMapper.selectList(ShipmentItem::getSaleContractCode, saleContractCodeList);
       if (CollUtil.isEmpty(shipmentItems)){
           return result;
       }
        List<Long> shipmentIdList = shipmentItems.stream().map(ShipmentItem::getShipmentId).distinct().toList();
       if (CollUtil.isEmpty(shipmentIdList)){
           return result;
       }
        List<ShipmentDO> shipmentDOS = shipmentMapper.selectBatchIds(shipmentIdList);
       if (CollUtil.isEmpty(shipmentDOS)){
           return result;
       }
        Map<Long, String> invoiceCodeMap = shipmentDOS.stream().collect(Collectors.toMap(ShipmentDO::getId, ShipmentDO::getInvoiceCode));
        Map<String, Map<Long, List<ShipmentItem>>> calcMap = shipmentItems.stream().collect(Collectors.groupingBy(ShipmentItem::getSaleContractCode, Collectors.groupingBy(ShipmentItem::getShipmentId)));
       if (CollUtil.isEmpty(calcMap)){
           return result;
       }
       calcMap.forEach((saleContractCode,shipmentItemMap)->{
           if (CollUtil.isEmpty(shipmentItemMap)){
               return;
           }
           List<ShipmentPriceEntity> shipmentPriceEntityList = new ArrayList<>();
           shipmentItemMap.forEach((shipmentId,shipmentItemList)->{
               if (CollUtil.isEmpty(shipmentItemList)){
                   return;
               }
               String invoiceCode = invoiceCodeMap.get(shipmentId);
               if (StrUtil.isEmpty(invoiceCode)){
                   return;
               }
               AtomicReference<String> currency = new AtomicReference<>(CalculationDict.CURRENCY_RMB);
               shipmentItemList.stream().filter(s->Objects.nonNull(s.getSaleUnitPrice())&&Objects.nonNull(s.getSaleUnitPrice().getAmount())).map(s->{
                   JsonAmount saleUnitPrice = s.getSaleUnitPrice();
                   Integer shippingQuantity = s.getShippingQuantity();
                   currency.set(saleUnitPrice.getCurrency());
                   return NumUtil.mul(saleUnitPrice.getAmount(),shippingQuantity);
               }).reduce(BigDecimal::add).ifPresent(s->{
                   shipmentPriceEntityList.add(new ShipmentPriceEntity().setInvoiceCode(invoiceCode).setReceivablePrice(new JsonAmount().setAmount(s).setCurrency(currency.get())));
               });
           });
           result.put(saleContractCode,shipmentPriceEntityList);
       });
       return result;
    }

    @Override
    public List<ShipmentItemDTO> getShipmentItemDTOListByShipmentId(Long shipmentId) {
        List<ShipmentItem> shipmentItems = shipmentItemMapper.selectList(ShipmentItem::getShipmentId, shipmentId);
        return BeanUtils.toBean(shipmentItems,ShipmentItemDTO.class);
    }

    @Override
    public List<ShipmentItemDTO> getShipmentItemDTOListByIdList(List<Long> shipmentItemIds) {
        if (CollUtil.isEmpty(shipmentItemIds)){
            return null;
        }
        List<ShipmentItem> shipmentItems = shipmentItemMapper.selectList(ShipmentItem::getId, shipmentItemIds);
        return BeanUtils.toBean(shipmentItems,ShipmentItemDTO.class);
    }

    @Override
    public void updateShipmentItem(List<ShipmentItemDTO> shipmentItemDTOList,boolean changeInvoiceConvertFlag) {
        if (CollUtil.isEmpty(shipmentItemDTOList)){
            return;
        }
        List<ShipmentItem> shipmentItemList =BeanUtils.toBean(shipmentItemDTOList,ShipmentItem.class);
        shipmentItemMapper.updateBatch(shipmentItemList);
        if (changeInvoiceConvertFlag){
            List<ShipmentDO> shipmentList = shipmentItemList.stream().map(s->{
                ShipmentDO shipmentDO = new ShipmentDO();
                shipmentDO.setId(s.getShipmentId());
                shipmentDO.setInvoiceNoticeFlag(BooleanEnum.NO.getValue());
                return shipmentDO;
            }).distinct().toList();
            if (CollUtil.isNotEmpty(shipmentList)){
                shipmentMapper.updateBatch(shipmentList);
            }
        }
    }

    @Override
    public void rollbackPurchaseCode(List<Long> saleContractItemIds, String purchaseCode, SimpleData simpleData) {
        if (CollUtil.isEmpty(saleContractItemIds)||StrUtil.isEmpty(purchaseCode)||Objects.isNull(simpleData)){
            return;
        }
        List<ShipmentItem> shipmentItems = shipmentItemMapper.selectList(ShipmentItem::getSaleContractItemId,saleContractItemIds);
        if (CollUtil.isEmpty(shipmentItems)){
            return;
        }
        shipmentItems.forEach(s->{
            s.setPurchaseContractCode(purchaseCode);
            s.setVenderId(simpleData.getId());
            s.setVenderCode(simpleData.getCode());
            s.setVenderName(simpleData.getName());
        });
        shipmentItemMapper.updateBatch(shipmentItems);
    }

    @Override
    public void batchManufactureSku(List<ManufactureSkuReqVO> manufactureSkuReqVOList) {
        if (CollUtil.isEmpty(manufactureSkuReqVOList)){
            return;
        }
        service.batchManufactureSku(manufactureSkuReqVOList);
    }

    @Override
    public Map<String, ShipmentDTO> getShipmentByForwarderFeeCodes(List<String> relationCodes) {
       Map<String,String> shipmentCodeMap =  forwarderFeeService.getShipmentCodeByCodes(relationCodes);
       if(shipmentCodeMap.isEmpty()){
           return null;
       }
       Map<String, ShipmentDTO> shipmentDTOMap = service.getByShipmentCodeList(shipmentCodeMap.values().stream().toList());
       if(shipmentDTOMap.isEmpty()){
           return null;
       }
        List<Long> shipmentIds = shipmentDTOMap.values().stream().map(ShipmentDTO::getId).distinct().toList();
        List<ShipmentItem> shipmentItems = shipmentItemMapper.selectList(ShipmentItem::getShipmentId,shipmentIds).stream().toList();
        if(shipmentItems.isEmpty()){
            return null;
        }
        Map<String, ShipmentDTO> res = new HashMap<>();
        relationCodes.forEach(s->{
            String shipmentCode = shipmentCodeMap.get(s);
            if(Objects.isNull(shipmentCode)){
                return;
            }
            ShipmentDTO shipmentDTO = shipmentDTOMap.get(shipmentCode);
            List<ShipmentItem> items = shipmentItems.stream().filter(shipmentItem -> Objects.equals(shipmentItem.getShipmentId(), shipmentDTO.getId())).toList();
            shipmentDTO.setChildren(BeanUtils.toBean(items,ShipmentItemDTO.class));
            res.put(s,shipmentDTO);
        });
        return res;
    }

    @Override
    public Map<Long, SimpleShipmentItemDTO> getSimpleShipmentItemDTOMap(List<String> saleContractCodeList) {
        if (CollUtil.isEmpty(saleContractCodeList)){
            return Map.of();
        }
        List<ShipmentItem> shipmentItems = shipmentItemMapper.selectList(new LambdaQueryWrapperX<ShipmentItem>().select(ShipmentItem::getUpdateTime,ShipmentItem::getShipmentId, ShipmentItem::getSaleContractItemId, ShipmentItem::getSkuId, ShipmentItem::getSplitBoxFlag, ShipmentItem::getSpecificationList).in(ShipmentItem::getSaleContractCode, saleContractCodeList));
        if (CollUtil.isEmpty(shipmentItems)){
            return Map.of();
        }
        List<Long> shipmentIdList = shipmentItems.stream().map(ShipmentItem::getShipmentId).distinct().toList();
        List<ShipmentDO> shipmentDOList = shipmentMapper.selectList(new LambdaQueryWrapperX<ShipmentDO>().select(ShipmentDO::getId).in(ShipmentDO::getId, shipmentIdList).ne(ShipmentDO::getStatus, ShippingStatusEnum.CASE_CLOSED.getValue()));
        if (CollUtil.isEmpty(shipmentDOList)){
            return Map.of();
        }
        List<Long> availableShipmentIdList = shipmentDOList.stream().map(ShipmentDO::getId).distinct().toList();
        return shipmentItems.stream().sorted(Comparator.comparing(ShipmentItem::getUpdateTime).reversed()).filter(s->availableShipmentIdList.contains(s.getShipmentId())).map(s->{
            SimpleShipmentItemDTO simpleShipmentItemDTO = new SimpleShipmentItemDTO();
            simpleShipmentItemDTO.setSkuId(s.getSkuId());
            simpleShipmentItemDTO.setSplitBoxFlag(s.getSplitBoxFlag());
            simpleShipmentItemDTO.setSpecificationList(s.getSpecificationList());
            simpleShipmentItemDTO.setSaleContractItemId(s.getSaleContractItemId());
            return simpleShipmentItemDTO;
        }).collect(Collectors.toMap(SimpleShipmentItemDTO::getSaleContractItemId, s->s, (o, n) -> o));
    }

    @Override
    public void rollbackShipmentByDeletedSettlement(List<Long> shipmentItemIdList) {
        if (CollUtil.isEmpty(shipmentItemIdList)){
            return;
        }
        List<ShipmentItem> shipmentItems = shipmentItemMapper.selectBatchIds(shipmentItemIdList);
        if (CollUtil.isEmpty(shipmentItems)){
            return;
        }
        shipmentItems.forEach(s->{
            s.setSettleOrderFlag(BooleanEnum.NO.getValue());
            s.setSettlementFormUser(new UserDept());
            s.setSettlementFormDate(null);
            s.setSettlementQuantity(0);
        });
        shipmentItemMapper.updateBatch(shipmentItems);
        List<Long> shipmentIdList = shipmentItems.stream().map(ShipmentItem::getShipmentId).distinct().toList();
        List<ShipmentItem> baseShipmentItemList = shipmentItemMapper.selectList(ShipmentItem::getShipmentId, shipmentIdList);
        Map<Long, List<ShipmentItem>> shipmentItemMap = baseShipmentItemList.stream().collect(Collectors.groupingBy(ShipmentItem::getShipmentId));
        List<ShipmentDO> updateShipmentList = new ArrayList<>();
        shipmentItemMap.forEach((shipmentId,shipmentItemList)->{
            if (CollUtil.isEmpty(shipmentItemList)){
                return;
            }
            boolean notTransformFlag = shipmentItemList.stream().noneMatch(s -> s.getSettleOrderFlag().equals(BooleanEnum.YES.getValue()));
            if (notTransformFlag){
                updateShipmentList.add(new ShipmentDO().setId(shipmentId).setSettleOrderFlag(BooleanEnum.NO.getValue()));
            }
        });
        if (CollUtil.isNotEmpty(updateShipmentList)){
            shipmentMapper.updateBatch(updateShipmentList);
        }
    }

    @Override
    public Map<Long, LocalDateTime> getEstDepartureTimeByPurchaseCodeList(List<Long> saleItemIdList) {
        if (CollUtil.isEmpty(saleItemIdList)){
            return Map.of();
        }
        List<ShipmentItem> shipmentItems = shipmentItemMapper.selectList(new LambdaQueryWrapperX<ShipmentItem>().in(ShipmentItem::getSaleContractItemId, saleItemIdList));
        if (CollUtil.isEmpty(shipmentItems)){
            return Map.of();
        }
        List<Long> shipmentIdList = shipmentItems.stream().map(ShipmentItem::getShipmentId).distinct().toList();
        List<ShipmentDO> shipmentDOS = shipmentMapper.selectList(new LambdaQueryWrapperX<ShipmentDO>().select(ShipmentDO::getId, ShipmentDO::getEstDepartureTime).in(ShipmentDO::getId, shipmentIdList).in(ShipmentDO::getStatus,List.of(ShippingStatusEnum.SHIPPED.getValue(),ShippingStatusEnum.COMPLETED.getValue())));
        if (CollUtil.isEmpty(shipmentDOS)){
            return Map.of();
        }
        Map<Long, ShipmentDO> shipmentDOMap = shipmentDOS.stream().collect(Collectors.toMap(ShipmentDO::getId, s -> s));
        Map<Long,LocalDateTime> result = new HashMap<>();
        shipmentItems.forEach(s->{
            ShipmentDO shipmentDO = shipmentDOMap.get(s.getShipmentId());
            if (Objects.isNull(shipmentDO)){
                return;
            }
            if (Objects.nonNull(shipmentDO.getEstDepartureTime())){
                result.put(s.getSaleContractItemId(),shipmentDO.getEstDepartureTime());
            }
        });
        return result;
    }

    @Override
    public void validateDeclarationFlag(List<String> shipmentCodeList) {
        service.validateDeclarationFlag(shipmentCodeList);
    }

    @Override
    public List<ShipmentDTO> getShipmentListByCode(List<String> shipmentCodeList) {
        return service.getShipmentListByCode(shipmentCodeList);
    }
}
