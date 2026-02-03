package com.syj.eplus.module.dms.convert.shipment;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.dict.CommonDict;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.syj.eplus.framework.common.dict.CalculationDict;
import com.syj.eplus.framework.common.entity.BatchChildVO;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.entity.StockChildVO;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.framework.common.enums.UnitPreOuterBoxTypeEnum;
import com.syj.eplus.framework.common.util.*;
import com.syj.eplus.module.dms.controller.admin.commodityinspection.vo.CommodityInspectionSaveReqVO;
import com.syj.eplus.module.dms.controller.admin.declaration.vo.DeclarationSaveReqVO;
import com.syj.eplus.module.dms.controller.admin.settlementform.vo.SettlementFormSaveReqVO;
import com.syj.eplus.module.dms.controller.admin.shipment.vo.ShipmentItemExportVO;
import com.syj.eplus.module.dms.controller.admin.shipment.vo.ShipmentRespVO;
import com.syj.eplus.module.dms.controller.admin.shipment.vo.ShipmentSaveReqVO;
import com.syj.eplus.module.dms.controller.admin.shipment.vo.change.ChangeShipmentRespVO;
import com.syj.eplus.module.dms.dal.dataobject.commodityinspectionitem.CommodityInspectionItem;
import com.syj.eplus.module.dms.dal.dataobject.declarationitem.DeclarationItem;
import com.syj.eplus.module.dms.dal.dataobject.settlementformitem.SettlementFormItem;
import com.syj.eplus.module.dms.dal.dataobject.shipment.ShipmentDO;
import com.syj.eplus.module.dms.dal.dataobject.shipmentchange.ShipmentChange;
import com.syj.eplus.module.dms.dal.dataobject.shipmentitem.ShipmentItem;
import com.syj.eplus.module.dms.dal.dataobject.shipmentplan.ShipmentPlanDO;
import com.syj.eplus.module.dms.dal.dataobject.shipmentplanitem.ShipmentPlanItem;
import com.syj.eplus.module.pms.api.packageType.dto.PackageTypeDTO;
import com.syj.eplus.module.pms.api.sku.dto.SkuNameDTO;
import com.syj.eplus.module.wms.api.stock.dto.StockDTO;
import com.syj.eplus.module.wms.api.stock.dto.StockLockRespVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.bpm.enums.ErrorCodeConstants.SHIPMENT_ITEM_SPEC_IS_NULL;


@Mapper
public interface ShipmentConvert {

    ShipmentConvert INSTANCE = Mappers.getMapper(ShipmentConvert.class);

    ShipmentRespVO convert(ShipmentDO shipmentDO);

    default ShipmentRespVO convertShipmentRespVO(ShipmentDO shipmentDO) {
        ShipmentRespVO shipmentRespVO = convert(shipmentDO);
        return shipmentRespVO;
    }

    ShipmentDO convertShipmentDO(ShipmentSaveReqVO saveReqVO);

    @Mapping(target = "children", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "shipmentPlanCode", source = "code")
    @Mapping(target = "shipDate", source = "estShipDate")
    ShipmentSaveReqVO convertShipmentSaveReqVO(ShipmentPlanDO shipmentPlanDO);

    @Mapping(target = "children", ignore = true)
    @Mapping(target = "id", ignore = true)
    CommodityInspectionSaveReqVO convertCommodityInspectionSaveReqVO(ShipmentDO shipmentDO);

    @Mapping(target = "id", ignore = true)
    CommodityInspectionItem convertCommodityInspectionItem(ShipmentItem shipmentItem);

    default SettlementFormItem convertSettlementFormItem(ShipmentItem shipmentItem, Map<Long, Integer> settlementQuantityMap){
        SettlementFormItem settlementFormItem = BeanUtils.toBean(shipmentItem, SettlementFormItem.class);
        settlementFormItem.setId(null);
        Integer baseQuantity = Objects.isNull(settlementFormItem.getSettlementQuantity()) ? 0 : settlementFormItem.getSettlementQuantity();
        settlementFormItem.setSettlementQuantity(baseQuantity + settlementQuantityMap.get(shipmentItem.getId()));
        settlementFormItem.setShipmentItemId(shipmentItem.getId());
        return settlementFormItem;
    }

    @Mapping(target = "children", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "shipmentCode",source = "code")
    SettlementFormSaveReqVO convertSettlementFormSaveReqVO(ShipmentDO shipmentDO);


    @Mapping(target = "children", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "shipmentCode",source = "code")
    DeclarationSaveReqVO convertDeclarationSaveReqVO(ShipmentDO shipmentDO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "shipmentItemId", source = "id")
    @Mapping(target = "declarationQuantity", source = "declarationQuantityCurrent")
    DeclarationItem convertDeclarationItem(ShipmentItem shipmentItem);

    default List<DeclarationItem> convertDeclarationItemList(List<ShipmentItem> shipmentItemList) {
        return CollectionUtils.convertList(shipmentItemList, s -> {
            DeclarationItem declarationItem = convertDeclarationItem(s);
            Integer declarationQuantity = declarationItem.getDeclarationQuantity();
            Integer boxCount = declarationItem.getBoxCount();
            declarationItem.setTotalGrossweight(JsonWeightUtil.mul(CalcSpecificationUtil.calcSpecificationTotalGrossweight(declarationItem.getSpecificationList()), boxCount));
            declarationItem.setTotalVolume(NumUtil.mul(CalcSpecificationUtil.calcSpecificationTotalVolume(declarationItem.getSpecificationList()), boxCount));
            declarationItem.setTotalNetweight(JsonWeightUtil.mul(CalcSpecificationUtil.calcSpecificationTotalNetweight(declarationItem.getSpecificationList()), boxCount));
            declarationItem.setDeclarationAmount(JsonAmountUtil.mul(declarationItem.getDeclarationUnitPrice(), declarationQuantity));
            return declarationItem;
        });
    }


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "declarationQuantity", source = "shippingQuantity")
    @Mapping(target = "declarationUnitPrice", source = "saleUnitPrice")
    @Mapping(target = "customsDeclarationNameEng", source = "nameEng")
    @Mapping(target = "declarationName", source = "name")
//    @Mapping(target = "declarationAmount", source = "saleUnitPrice")
    ShipmentItem convertShipmentItem(ShipmentPlanItem shipmentPlanItem);


    default List<ShipmentItem> convertShipmentItemList(List<ShipmentPlanItem> shipmentPlanItemList) {
        return CollectionUtils.convertList(shipmentPlanItemList, s -> {
            ShipmentItem shipmentItem = convertShipmentItem(s);
            shipmentItem.setUniqueCode(IdUtil.fastSimpleUUID());
            shipmentItem.setSourceUniqueCode(s.getUniqueCode());
            shipmentItem.setUnitPerOuterbox(UnitPreOuterBoxTypeEnum.getValueByName("CTNS"));
            shipmentItem.setDescription(s.getDescription());
            shipmentItem.setDescriptionEng(s.getDescriptionEng());

            return shipmentItem;
        });
    }


    default List<ShipmentRespVO> convertShipmentItemRespVO(List<ShipmentDO> shipmentDOList, Map<Long, List<ShipmentItem>> shipmentItemMap,List<PackageTypeDTO> packageTypeList,Map<String, List<Long>> saleCompanyMap) {
        return CollectionUtils.convertList(shipmentDOList, shipmentDO -> {
            Long shipmentId = shipmentDO.getId();
            ShipmentRespVO shipmentRespVO = convert(shipmentDO);
            if (CollUtil.isNotEmpty(shipmentItemMap)) {
                List<ShipmentItem> shipmentItemList = shipmentItemMap.get(shipmentId);
                if (CollUtil.isNotEmpty(shipmentItemList)) {
                    shipmentItemList.forEach(s->{
//                        if (StrUtil.isNotEmpty(s.getPurchaseContractCode()) && invoicedPurchaseCodeSet.contains(s.getPurchaseContractCode())){
//                            s.setInvoiceStatus(InvoiceStatusEnum.ISSUED.getValue());
//                        }
                        if (CollUtil.isNotEmpty(saleCompanyMap) && StrUtil.isNotEmpty(s.getSaleContractCode())){
                            List<Long> saleCompanyIdList = saleCompanyMap.get(s.getSaleContractCode());
                            if (CollUtil.isNotEmpty(saleCompanyIdList)){
                                s.setCompanyIdList(saleCompanyIdList);
                            }
                        }
                        if(CollUtil.isNotEmpty(packageTypeList) && CollUtil.isNotEmpty(s.getPackageType())){
                            List<Long> distinctPackgeType = s.getPackageType().stream().distinct().toList();
                            List<PackageTypeDTO> tempPackageTypeList = packageTypeList.stream().filter(pt->distinctPackgeType.contains(pt.getId())).toList();
                            if(CollUtil.isNotEmpty(tempPackageTypeList)){
                                List<String> packageTypeNameList = tempPackageTypeList.stream().map(PackageTypeDTO::getName).distinct().toList();
                                s.setPackageTypeName(String.join(",",packageTypeNameList));
                                List<String> packageTypeNameEngList = tempPackageTypeList.stream().map(PackageTypeDTO::getNameEng).distinct().toList();
                                s.setPackageTypeEngName(String.join(",",packageTypeNameEngList));
                            }
                        }
                    });
                    shipmentRespVO.setSaleContractCode(shipmentItemList.stream().map(ShipmentItem::getSaleContractCode).filter(StrUtil::isNotEmpty).distinct().collect(Collectors.joining(CommonDict.COMMA)));
                    shipmentRespVO.setCustName(shipmentItemList.stream().map(ShipmentItem::getCustName).filter(StrUtil::isNotEmpty).distinct().collect(Collectors.joining(CommonDict.COMMA)));
                    shipmentRespVO.setChildren(shipmentItemList);
                    String custPo = shipmentItemList.stream().map(ShipmentItem::getCustPo).distinct().collect(Collectors.joining(CommonDict.COMMA));
                    shipmentRespVO.setCustPo(custPo);
                }
            }
            return shipmentRespVO;
        });
    }

    @Mapping(target = "shipmentCode", source = "code")
    ShipmentChange convertShipmentChange(ShipmentRespVO shipmentRespVO);

    @Mapping(target = "code", source = "shipmentCode")
    ShipmentDO convertShipmentDO(ShipmentChange shipmentChange);

    @Mapping(target = "code", source = "shipmentCode")
    ShipmentRespVO convertShipmentRespVO(ShipmentChange shipmentChange);


    ChangeShipmentRespVO convertChangeShipmentRespVO(ShipmentChange shipmentChange);

    List<ChangeShipmentRespVO> convertChangeShipmentRespVOList(List<ShipmentChange> shipmentChangeList);

    BatchChildVO convertBatchildByStock(StockDTO stockDTO);

    default List<BatchChildVO> convertBatchildByStockList(ShipmentItem shipmentItem,List<StockDTO> stockDTOList){
        return CollectionUtils.convertList(stockDTOList, s->{
            BatchChildVO batchChildVO = convertBatchildByStock(s);
            batchChildVO.setInboundDate(s.getReceiptTime());
            // 可拉柜数量 = 可用数量+在制数量
            batchChildVO.setAvailableCabinetQuantity(s.getAvailableQuantity() + s.getProducingQuantity());
            Integer boxCount = NumberUtil.div(batchChildVO.getAvailableCabinetQuantity(), batchChildVO.getQtyPerOuterbox(), 0, RoundingMode.UP).intValue();
            batchChildVO.setBoxCount(boxCount);
            batchChildVO.setUsedQuantity(Math.min(s.getAvailableQuantity()+s.getProducingQuantity()-s.getCabinetQuantity(),shipmentItem.getShippingQuantity() - shipmentItem.getRealLockQuantity()));
            batchChildVO.setStockMethod(2);
            batchChildVO.setVenderCode(shipmentItem.getVenderCode());
            batchChildVO.setVenderName(shipmentItem.getVenderName());
            batchChildVO.setShippedAddress(shipmentItem.getShippedAddress());
            batchChildVO.setVenderId(shipmentItem.getVenderId());
            batchChildVO.setSplitBoxFlag(shipmentItem.getSplitBoxFlag());
            batchChildVO.setSpecificationList(shipmentItem.getSpecificationList());
            batchChildVO.setShipmentItemId(shipmentItem.getId());
            batchChildVO.setQtyPerOuterbox(shipmentItem.getQtyPerOuterbox());
            return batchChildVO;
        });
    }

    @Mapping(target = "availableCabinetQuantity",source = "lockQuantity")
    BatchChildVO convertBatchildByStockLock(StockLockRespVO stockLockRespVO);

    default List<BatchChildVO> convertBatchildByStockLockList(ShipmentItem shipmentItem, List<StockLockRespVO> stockDTOList, Map<Long, StockDTO> stockMap){
        AtomicReference<Integer> realLockQuantity = new AtomicReference<>(shipmentItem.getRealLockQuantity());
        if (Objects.isNull(realLockQuantity.get())||realLockQuantity.get() == 0){
            return List.of();
        }
        return CollectionUtils.convertList(stockDTOList,s->{
            if (realLockQuantity.get() == 0){
                return null;
            }
            // 过滤已经转过出运的锁定库存
            if (Objects.equals(s.getLockQuantity(), s.getCabinetQuantity())){
                return null;
            }
            BatchChildVO batchChildVO = convertBatchildByStockLock(s);
            if (CollUtil.isEmpty(stockMap)){
                return batchChildVO;
            }
            StockDTO stockDTO = stockMap.get(s.getStockId());
            if (Objects.isNull(stockDTO)){
                return batchChildVO;
            }
            Integer lockQuantity = Objects.isNull(s.getLockQuantity())?0:s.getLockQuantity()-s.getCabinetQuantity();
            if (lockQuantity >= realLockQuantity.get()){
               int usedQuantity = realLockQuantity.get();
                batchChildVO.setUsedQuantity(usedQuantity);
                realLockQuantity.set(0);
            }else {
                batchChildVO.setUsedQuantity(lockQuantity);
                realLockQuantity.set(realLockQuantity.get() - lockQuantity);
            }

            batchChildVO.setPurchaseContractCode(stockDTO.getPurchaseContractCode());
            batchChildVO.setPurchaseContractId(stockDTO.getPurchaseContractId());
            batchChildVO.setVenderId(shipmentItem.getVenderId());
            batchChildVO.setVenderCode(shipmentItem.getVenderCode());
            batchChildVO.setVenderName(shipmentItem.getVenderName());
            batchChildVO.setInboundDate(stockDTO.getReceiptTime());
            batchChildVO.setShippedAddress(shipmentItem.getShippedAddress());
            batchChildVO.setWarehouseId(stockDTO.getWarehouseId());
            batchChildVO.setWarehouseName(stockDTO.getWarehouseName());
            batchChildVO.setQtyPerInnerbox(stockDTO.getQtyPerInnerbox());
            batchChildVO.setQtyPerOuterbox(stockDTO.getQtyPerOuterbox());
            batchChildVO.setSpecificationList(shipmentItem.getSpecificationList());
            batchChildVO.setSplitBoxFlag(shipmentItem.getSplitBoxFlag());
            batchChildVO.setQtyPerOuterbox(shipmentItem.getQtyPerOuterbox());
            batchChildVO.setStockMethod(1);
            batchChildVO.setShipmentItemId(shipmentItem.getId());
            batchChildVO.setQtyPerOuterbox(shipmentItem.getQtyPerOuterbox());
            return batchChildVO;
        });
    }

    default List<StockChildVO> convertStockChildVOListByStockList(List<StockDTO> stockDTOList,Map<String, String> skuNameCache,Map<String, Integer> shippingQuantityMap){
        if (CollUtil.isEmpty(stockDTOList)){
            return List.of();
        }
        Map<String, List<StockDTO>> stockMap = stockDTOList.stream().filter(s->s.getAvailableQuantity()>0).collect(Collectors.groupingBy(s -> s.getSkuId() + CommonDict.BASE_SNAKE + s.getSkuCode()  + CommonDict.BASE_SNAKE + s.getSaleContractCode()));
        List<StockChildVO> result = new ArrayList<>();
        stockMap.forEach((k,v)->{
            List<String> keyList = SplitUtil.splitToStringList(CommonDict.BASE_SNAKE, k);
            String skuId = keyList.get(0);
            String skuCode = keyList.get(1);
            String saleContractCode = keyList.get(2);
            StockChildVO stockChildVO = new StockChildVO();
            stockChildVO.setSkuId(Long.valueOf(skuId));
            stockChildVO.setSkuCode(skuCode);
            stockChildVO.setSkuName(skuNameCache.get(skuCode));
            stockChildVO.setSaleContractCode(saleContractCode);
            stockChildVO.setShippingQuantity(shippingQuantityMap.get(saleContractCode));
//            stockChildVO.setChildren(convertBatchildByStockList(v));
            result.add(stockChildVO);
        });
        return result;
    }

    default List<StockChildVO> convertStockChildVOListByStockLockList(List<StockLockRespVO> stockLockRespVOList,Map<String, String> skuNameCache,Map<String, Integer> shippingQuantityMap){
        if (CollUtil.isEmpty(stockLockRespVOList)){
            return List.of();
        }
        Map<String, List<StockLockRespVO>> stockLockMap = stockLockRespVOList.stream().filter(s->s.getLockQuantity()>0).collect(Collectors.groupingBy(s -> s.getSaleContractCode() + CommonDict.BASE_SNAKE + s.getSkuCode()));
        List<StockChildVO> result = new ArrayList<>();
        stockLockMap.forEach((k,v)->{
            List<String> keyList = SplitUtil.splitToStringList(CommonDict.BASE_SNAKE, k);
            String skuCode = keyList.get(1);
            String saleContractCode = keyList.get(0);
            StockChildVO stockChildVO = new StockChildVO();
            stockChildVO.setSkuCode(skuCode);
            stockChildVO.setSkuName(skuNameCache.get(keyList.get(1)));
            stockChildVO.setSaleContractCode(saleContractCode);
            stockChildVO.setShippingQuantity(shippingQuantityMap.get(saleContractCode));
//            stockChildVO.setChildren(convertBatchildByStockLockList(v));
            result.add(stockChildVO);
        });
        return result;
    }

    default List<StockChildVO> convertStockChildVOList(List<StockLockRespVO> stockLockRespVOList, List<StockDTO> stockDTOList, Map<String, String> skuNameCache, Map<String, Integer> shippingQuantityMap){
        List<StockChildVO> result = new ArrayList<>();
        if (CollUtil.isNotEmpty(stockLockRespVOList)){
            List<StockChildVO> stockChildVOList = convertStockChildVOListByStockLockList(stockLockRespVOList,skuNameCache,shippingQuantityMap);
            if (CollUtil.isNotEmpty(stockChildVOList)){
                result.addAll(stockChildVOList);
            }
        }
//        if (CollUtil.isNotEmpty(stockDTOList)){
//            List<StockChildVO> stockChildVOList = convertStockChildVOListByStockList(stockDTOList,skuNameCache,shippingQuantityMap);
//            if (CollUtil.isNotEmpty(stockChildVOList)){
//                result.addAll(stockChildVOList);
//            }
//        }
        return result;
    }


    default List<StockChildVO> convertStockChildVOByItems(List<ShipmentItem> shipmentItemList,Map<String, SkuNameDTO> skuNameCache){
        return CollectionUtils.convertList(shipmentItemList,  s -> {
            StockChildVO stockChildVO = convertStockChildVOByShipmentItem(s);
            String skuCode = s.getSkuCode();
            SkuNameDTO skuNameDTO = skuNameCache.get(skuCode);
            if (Objects.nonNull(skuNameDTO)){
                stockChildVO.setSkuName(skuNameDTO.getDeclarationName());
            }
            return stockChildVO;
        });
    }

    @Mapping(target = "saleContractCode", source = "sourceSaleContractCode")
    @Mapping(target = "shipmentItemId", source = "id")
    StockChildVO convertStockChildVOByShipmentItem(ShipmentItem shipmentItem);

    default List<StockChildVO> convertStockChildVOListByShipmentItem(Map<String,List<StockLockRespVO>> stockLockRespVOMap,Map<String,List<StockDTO>> stockDTOMap,List<ShipmentItem> shipmentItemList,
                                                                     Map<String, SkuNameDTO> skuNameCache,String billLadingNum,Map<Long, StockDTO> stockMap,Map<String, Integer> skuTypeMap){
        return shipmentItemList.stream().map(s->{
            String skuCode = s.getSkuCode();
            String purchaseContractCode = s.getPurchaseContractCode();
            StockChildVO stockChildVO = convertStockChildVOByShipmentItem(s);
            stockChildVO.setShipmentItemId(s.getId());
            stockChildVO.setSourceUniqueCode(s.getUniqueCode());
            stockChildVO.setConverNoticeFlag(s.getConverNoticeFlag());
            SkuNameDTO skuNameDTO = skuNameCache.get(skuCode);
            if (Objects.nonNull(skuNameDTO)){
                stockChildVO.setSkuName(skuNameDTO.getDeclarationName());
            }
            stockChildVO.setReferenceNumber(billLadingNum);
            stockChildVO.setSkuType(skuTypeMap.get(skuCode));
            stockChildVO.setSaleContractItemId(s.getSaleContractItemId());
            // 查询产品明细库存
            List<BatchChildVO> batchChildVOList = new ArrayList<>();
            // 库存表中明细
            List<StockDTO> stockDTOS = stockDTOMap.get(s.getSourceSaleContractCode()+skuCode+purchaseContractCode);
            // 锁定库存明细
            List<StockLockRespVO> stockLockRespVOS = stockLockRespVOMap.get(s.getSourceSaleContractCode()+s.getSaleContractItemId());
            // 拆分采购出运不需要查询库存信息
            if (CollUtil.isNotEmpty(stockDTOS)&& BooleanEnum.NO.getValue().equals(s.getProcessFlag())){
                stockDTOS = stockDTOS.stream().sorted(Comparator.comparing(StockDTO::getBatchCode)).toList();
                List<BatchChildVO> stockChildList = convertBatchildByStockList(s,stockDTOS);
                if (CollUtil.isNotEmpty(stockChildList)) {
                    batchChildVOList.addAll(stockChildList.stream().filter(x->x.getAvailableCabinetQuantity()>0).collect(Collectors.toList()));
                }
            }
            // 锁定库存明细
            if (CollUtil.isNotEmpty(stockLockRespVOS)){
                // 仅需添加未在库存中的锁定批次即可，避免重复数据
                List<String> batchCodeList = batchChildVOList.stream().map(BatchChildVO::getBatchCode).toList();
                List<BatchChildVO> stockLockChildList = convertBatchildByStockLockList(s,stockLockRespVOS, stockMap);
                if (CollUtil.isNotEmpty(stockLockChildList)){
                    batchChildVOList.addAll(stockLockChildList.stream().filter(x->Objects.nonNull(x)&&!batchCodeList.contains(x.getBatchCode())).toList());
                }
            }
            stockChildVO.setChildren(batchChildVOList);
            stockChildVO.setAvailableCabinetQuantity(
                    batchChildVOList.stream().map(BatchChildVO::getAvailableCabinetQuantity).filter(Objects::nonNull).reduce(0, Integer::sum)
            );
            stockChildVO.setProducingQuantity(
                    batchChildVOList.stream().map(BatchChildVO::getProducingQuantity).filter(Objects::nonNull).reduce(0, Integer::sum)
            );
            return stockChildVO;
        }).toList();
    }
    @Mapping(target = "totalGrossweight", ignore = true)
    @Mapping(target = "outerboxNetweight", ignore = true)
    @Mapping(target = "totalNetweight", ignore = true)
    @Mapping(target = "manager", ignore = true)
    ShipmentItemExportVO convertShipmentItemExportVO(ShipmentItem shipmentItem);
    default List<ShipmentItemExportVO> convertShipmentItemExportVO(List<ShipmentItem> shipmentItemList,ShipmentDO shipmentDO,String exportType){
        if (CollUtil.isEmpty(shipmentItemList)){
            return List.of();
        }
        if(!exportType.equals("hsCode")){
            return shipmentItemList.stream().map(shipmentItem -> {
                List<JsonSpecificationEntity> specificationList = shipmentItem.getSpecificationList();
                if (CollUtil.isEmpty(specificationList)){
                    throw exception(SHIPMENT_ITEM_SPEC_IS_NULL);
                }
                return specificationList.stream().map(s->{
                    ShipmentItemExportVO shipmentItemExportVO = convertShipmentItemExportVO(shipmentItem);
                    shipmentItemExportVO.setName(shipmentItem.getName());
                    if(shipmentItem.getBoxCount()!=null){
                        shipmentItemExportVO.setBoxCountLabel(shipmentItem.getBoxCount() + UnitPreOuterBoxTypeEnum.getNameByValue(shipmentItem.getUnitPerOuterbox()));
                    }
                    //外箱毛重
                    BigDecimal outerboxGrossweight = BigDecimal.ZERO;
                    JsonWeight jsonOuterboxGrossweight = s.getOuterboxGrossweight();
                    if (Objects.nonNull(jsonOuterboxGrossweight)){
                        outerboxGrossweight = Objects.isNull(jsonOuterboxGrossweight.getWeight())?BigDecimal.ZERO:JsonWeightUtil.convertToKg(jsonOuterboxGrossweight).getWeight();
                    }
                    shipmentItemExportVO.setOuterboxGrossweight(outerboxGrossweight.setScale(CalculationDict.TWO, RoundingMode.HALF_UP).stripTrailingZeros());
                    //总毛重
                    BigDecimal totalGrossweight = BigDecimal.ZERO;
                    if (Objects.nonNull(jsonOuterboxGrossweight)){
                        totalGrossweight = Objects.isNull(jsonOuterboxGrossweight.getWeight())?BigDecimal.ZERO: JsonWeightUtil.convertToKg(jsonOuterboxGrossweight).getWeight().multiply(BigDecimal.valueOf(shipmentItem.getBoxCount()));
                    }
                    shipmentItemExportVO.setTotalGrossweight(totalGrossweight.setScale(CalculationDict.TWO, RoundingMode.HALF_UP).stripTrailingZeros());
                    //单箱毛重
                    if(shipmentItem.getBoxCount()>0){
                        shipmentItemExportVO.setSingleGrossweight(NumberUtil.div(totalGrossweight,shipmentItem.getBoxCount()).setScale(CalculationDict.TWO, RoundingMode.HALF_UP).stripTrailingZeros());
                    }
                    //外箱净重
                    BigDecimal outerboxNetweight = BigDecimal.ZERO;
                    JsonWeight jsonOuterboxNetweight = s.getOuterboxNetweight();
                    if (Objects.nonNull(jsonOuterboxNetweight)){
                        outerboxNetweight = Objects.isNull(jsonOuterboxNetweight.getWeight())?BigDecimal.ZERO:JsonWeightUtil.convertToKg(jsonOuterboxNetweight).getWeight();
                    }
                    shipmentItemExportVO.setOuterboxNetweight(outerboxNetweight.setScale(CalculationDict.TWO, RoundingMode.HALF_UP).stripTrailingZeros());
                    //总净重
                    BigDecimal totalNetweight = BigDecimal.ZERO;
                    if (Objects.nonNull(jsonOuterboxNetweight)){
                        totalNetweight = Objects.isNull(jsonOuterboxNetweight.getWeight())?BigDecimal.ZERO:JsonWeightUtil.convertToKg(jsonOuterboxNetweight).getWeight().multiply(BigDecimal.valueOf(shipmentItem.getBoxCount()));
                    }
                    //单箱净重
                    if(shipmentItem.getBoxCount()>0){
                        shipmentItemExportVO.setSingleNetweight(NumberUtil.div(totalNetweight,shipmentItem.getBoxCount()).setScale(CalculationDict.TWO, RoundingMode.HALF_UP).stripTrailingZeros());
                    }
                    shipmentItemExportVO.setTotalNetweight(totalNetweight.setScale(CalculationDict.TWO, RoundingMode.HALF_UP).stripTrailingZeros());
                    shipmentItemExportVO.setTotalVolume(VolumeUtil.processVolume(shipmentItem.getTotalVolume()));
                    shipmentItemExportVO.setReceivePerson(shipmentDO.getReceivePerson());
                    shipmentItemExportVO.setNotifyPerson(shipmentDO.getNotifyPerson());
                    shipmentItemExportVO.setFrontShippingMark(shipmentDO.getFrontShippingMark());
                    shipmentItemExportVO.setInvoiceCode(shipmentDO.getInvoiceCode());
                    shipmentItemExportVO.setOuterboxHeight(s.getOuterboxHeight().setScale(2, RoundingMode.HALF_UP).stripTrailingZeros());
                    shipmentItemExportVO.setOuterboxWidth(s.getOuterboxWidth().setScale(2, RoundingMode.HALF_UP).stripTrailingZeros());
                    shipmentItemExportVO.setOuterboxLength(s.getOuterboxLength().setScale(2, RoundingMode.HALF_UP).stripTrailingZeros());
                    shipmentItemExportVO.setOuterboxVolume(VolumeUtil.processVolume(s.getOuterboxVolume()));
                    if(Objects.nonNull(shipmentItem.getManager())){
                        shipmentItemExportVO.setManager(shipmentItem.getManager().getNickname());
                    }
                    shipmentItemExportVO.setSpec( s.getOuterboxLength().setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString() + "*" +
                            s.getOuterboxWidth().setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString() + "*" +
                            s.getOuterboxHeight().setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());
                    return shipmentItemExportVO;
                }).toList();
            }).flatMap(List::stream).toList();
        }else{
            return shipmentItemList.stream().map(shipmentItem -> {
                List<JsonSpecificationEntity> specificationList = shipmentItem.getSpecificationList();
                if (CollUtil.isEmpty(specificationList)){
                    throw exception(SHIPMENT_ITEM_SPEC_IS_NULL);
                }
                ShipmentItemExportVO shipmentItemExportVO = convertShipmentItemExportVO(shipmentItem);
                shipmentItemExportVO.setInvoiceCode(shipmentDO.getInvoiceCode());
                //总毛重
                BigDecimal totalGrossweight = BigDecimal.ZERO;
                JsonWeight jsonOuterboxGrossweight = CalcSpecificationUtil.calcSpecificationTotalGrossweight(specificationList);
                if (Objects.nonNull(jsonOuterboxGrossweight)){
                    totalGrossweight = Objects.isNull(jsonOuterboxGrossweight.getWeight())?BigDecimal.ZERO: JsonWeightUtil.convertToKg(jsonOuterboxGrossweight).getWeight().multiply(BigDecimal.valueOf(shipmentItem.getBoxCount()));
                }
                shipmentItemExportVO.setTotalGrossweight(totalGrossweight.setScale(CalculationDict.TWO, RoundingMode.HALF_UP).stripTrailingZeros().stripTrailingZeros());
                //总净重
                BigDecimal totalNetweight = BigDecimal.ZERO;
                JsonWeight jsonOuterboxNetweight = CalcSpecificationUtil.calcSpecificationTotalNetweight(specificationList);
                if (Objects.nonNull(jsonOuterboxNetweight)){
                    totalNetweight = Objects.isNull(jsonOuterboxNetweight.getWeight())?BigDecimal.ZERO:JsonWeightUtil.convertToKg(jsonOuterboxNetweight).getWeight().multiply(BigDecimal.valueOf(shipmentItem.getBoxCount()));
                }
                shipmentItemExportVO.setTotalNetweight(totalNetweight.setScale(CalculationDict.TWO, RoundingMode.HALF_UP).stripTrailingZeros());
                //总体积
                shipmentItemExportVO.setTotalVolume(VolumeUtil.processVolume(shipmentItem.getTotalVolume()));
                //件数
                shipmentItemExportVO.setBoxCount((int) NumberUtil.mul(shipmentItem.getBoxCount().intValue(),specificationList.size()));
                return shipmentItemExportVO;
            }).collect(Collectors.toList());
        }
    }

    default List<SettlementFormItem> convertSettlementFormItemList(List<ShipmentItem> shipmentItem, Map<Long, Integer> settlementQuantityMap){
        return CollectionUtils.convertList(shipmentItem, s-> convertSettlementFormItem(s,settlementQuantityMap));
    }
}
