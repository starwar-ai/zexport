package com.syj.eplus.module.dms.convert.settlementform;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import com.syj.eplus.framework.common.dict.CalculationDict;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.enums.UnitPreOuterBoxTypeEnum;
import com.syj.eplus.framework.common.util.CalcSpecificationUtil;
import com.syj.eplus.framework.common.util.NumUtil;
import com.syj.eplus.module.dms.controller.admin.settlementform.vo.SettlementFormAddSubItemExportVO;
import com.syj.eplus.module.dms.controller.admin.settlementform.vo.SettlementFormItemExportVO;
import com.syj.eplus.module.dms.controller.admin.settlementform.vo.SettlementFormRespVO;
import com.syj.eplus.module.dms.controller.admin.settlementform.vo.SettlementFormSaveReqVO;
import com.syj.eplus.module.dms.dal.dataobject.settlementform.SettlementFormDO;
import com.syj.eplus.module.dms.dal.dataobject.settlementformitem.SettlementFormItem;
import com.syj.eplus.module.pms.api.packageType.dto.PackageTypeDTO;
import com.syj.eplus.module.sms.api.dto.AddSubItemDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Mapper
public interface SettlementFormConvert {

    SettlementFormConvert INSTANCE = Mappers.getMapper(SettlementFormConvert.class);

    SettlementFormRespVO convert(SettlementFormDO settlementFormDO);

    default SettlementFormRespVO convertSettlementFormRespVO(SettlementFormDO settlementFormDO) {
        SettlementFormRespVO settlementFormRespVO = convert(settlementFormDO);
        return settlementFormRespVO;
    }

    default List<SettlementFormRespVO> convertSettlementFormRespVO(List<SettlementFormDO> settlementFormDOList, Map<Long, List<SettlementFormItem>> settlementFormItemMap, List<PackageTypeDTO> packageTypeList) {
        return CollectionUtils.convertList(settlementFormDOList, settlementFormDO -> {
            Long declarationId = settlementFormDO.getId();
            SettlementFormRespVO settlementFormRespVO = convert(settlementFormDO);
            if (CollUtil.isNotEmpty(settlementFormItemMap)) {
                List<SettlementFormItem> declarationItemList = settlementFormItemMap.get(declarationId);
                if (CollUtil.isNotEmpty(declarationItemList)) {
                    declarationItemList.forEach(s -> {
                        if (CollUtil.isNotEmpty(packageTypeList) && CollUtil.isNotEmpty(s.getPackageType())) {
                            List<Long> distinctPackgeType = s.getPackageType().stream().distinct().toList();
                            List<PackageTypeDTO> tempPackageTypeList = packageTypeList.stream().filter(pt -> distinctPackgeType.contains(pt.getId())).toList();
                            if (CollUtil.isNotEmpty(tempPackageTypeList)) {
                                List<String> packageTypeNameList = tempPackageTypeList.stream().map(PackageTypeDTO::getName).distinct().toList();
                                s.setPackageTypeName(String.join(",", packageTypeNameList));
                                List<String> packageTypeNameEngList = tempPackageTypeList.stream().map(PackageTypeDTO::getNameEng).distinct().toList();
                                s.setPackageTypeEngName(String.join(",", packageTypeNameEngList));
                            }
                        }
                    });
                    settlementFormRespVO.setChildren(declarationItemList);
                }
            }
            return settlementFormRespVO;
        });
    }


    SettlementFormDO convertSettlementFormDO(SettlementFormSaveReqVO saveReqVO);

    SettlementFormItemExportVO convertSettlementFormItemExportVO(SettlementFormItem settlementFormItem);

    default List<SettlementFormItemExportVO> convertSettlementFormItemExportVOList(List<SettlementFormItem> settlementFormItems) {
//                return CollectionUtils.convertList(settlementFormItem, s -> {
//                        SettlementFormItemExportVO settlementFormItemExportVO =  convertSettlementFormItemExportVO(s);
//                        if(s.getPurchaseTotalQuantity()!=null && s.getPurchaseWithTaxPrice().getAmount()!=null){
//                                settlementFormItemExportVO.setPurchaseTotalAmountAmount(NumUtil.mul(s.getPurchaseTotalQuantity(), s.getPurchaseWithTaxPrice().getAmount()));
//                        }
//                        if(s.getUnitPerOuterbox()!=null){
//                                settlementFormItemExportVO.setUnitPerOuterbox(UnitPreOuterBoxTypeEnum.getNameByValue(s.getUnitPerOuterbox()));
//                        }
//                        return settlementFormItemExportVO;
//                });
        if (CollUtil.isEmpty(settlementFormItems)) {
            return List.of();
        }
        List<SettlementFormItemExportVO> result = new ArrayList<>();
        settlementFormItems.forEach(settlementFormItem -> {
            List<JsonSpecificationEntity> specificationList = settlementFormItem.getSpecificationList();
            if (CollUtil.isEmpty(specificationList)) {
                SettlementFormItemExportVO settlementFormItemExportVO = convertSettlementFormItemExportVO(settlementFormItem);
                if (settlementFormItem.getPurchaseTotalQuantity() != null && settlementFormItem.getPurchaseWithTaxPrice().getAmount() != null) {
                    settlementFormItemExportVO.setPurchaseTotalAmountAmount(NumUtil.mul(settlementFormItem.getPurchaseTotalQuantity(), settlementFormItem.getPurchaseWithTaxPrice().getAmount()));
                }
                if (settlementFormItem.getUnitPerOuterbox() != null) {
                    settlementFormItemExportVO.setUnitPerOuterbox(UnitPreOuterBoxTypeEnum.getNameByValue(settlementFormItem.getUnitPerOuterbox()));
                }
                settlementFormItemExportVO.setShippingQuantity(settlementFormItem.getShippingQuantity());
                result.add(settlementFormItemExportVO);

            } else {
                for (int i = 0; i < specificationList.size(); i++) {
                    SettlementFormItemExportVO settlementFormItemExportVO = transformExportVO(settlementFormItem, specificationList, i);
                    settlementFormItemExportVO.setShippingQuantity(settlementFormItem.getSettlementQuantity());
                    result.add(settlementFormItemExportVO);

                }
            }
        });
        return result;
    }
    private SettlementFormItemExportVO transformExportVO(SettlementFormItem settlementFormItem,List<JsonSpecificationEntity> specificationList,Integer index){
        SettlementFormItemExportVO settlementFormItemExportVO = convertSettlementFormItemExportVO(settlementFormItem);
        if (settlementFormItem.getPurchaseTotalQuantity() != null && settlementFormItem.getPurchaseWithTaxPrice().getAmount() != null) {
            settlementFormItemExportVO.setPurchaseTotalAmountAmount(NumUtil.mul(settlementFormItem.getPurchaseTotalQuantity(), settlementFormItem.getPurchaseWithTaxPrice().getAmount()));
        }
        if (settlementFormItem.getUnitPerOuterbox() != null) {
            settlementFormItemExportVO.setUnitPerOuterbox(UnitPreOuterBoxTypeEnum.getNameByValue(settlementFormItem.getUnitPerOuterbox()));
        }
        JsonSpecificationEntity specificationEntity = specificationList.get(index);
        if (index > 0) {
            JsonAmount jsonAmount = new JsonAmount().setAmount(BigDecimal.ZERO).setCurrency(CalculationDict.CURRENCY_RMB);
            settlementFormItemExportVO.setPurchaseUnitPriceAmount(BigDecimal.ZERO);
            settlementFormItemExportVO.setPurchaseTotalAmountAmount(BigDecimal.ZERO);
            settlementFormItemExportVO.setSaleAmount(jsonAmount);
            settlementFormItemExportVO.setSaleAmountAmount(BigDecimal.ZERO);
            settlementFormItemExportVO.setSaleUnitPriceAmount(BigDecimal.ZERO);
            settlementFormItemExportVO.setTaxRefundPriceAmount(jsonAmount);
            settlementFormItemExportVO.setTaxRefundPriceAmountAmount(BigDecimal.ZERO);
        }
        BigDecimal totalVolume = CalcSpecificationUtil.calcTotalVolumeByBoxCount(List.of(specificationEntity), BigDecimal.valueOf(settlementFormItem.getBoxCount()));
        JsonWeight grossweight = CalcSpecificationUtil.calcTotalGrossweightByBoxCount(List.of(specificationEntity), BigDecimal.valueOf(settlementFormItem.getBoxCount()));
        JsonWeight netWeight = CalcSpecificationUtil.calcTotalNetWeightByBoxCount(List.of(specificationEntity), BigDecimal.valueOf(settlementFormItem.getBoxCount()));
        settlementFormItemExportVO.setSaleAmountAmount(settlementFormItem.getSaleAmount().getAmount());
        settlementFormItemExportVO.setTotalVolume(totalVolume);
        settlementFormItemExportVO.setOuterboxVolume(CalcSpecificationUtil.calcSpecificationTotalVolume(List.of(specificationEntity)));
        settlementFormItemExportVO.setTotalGrossweight(grossweight);
        settlementFormItemExportVO.setTotalGrossweightWeight(grossweight.getWeight());
        settlementFormItemExportVO.setTotalNetweight(netWeight);
        settlementFormItemExportVO.setTotalNetweightWeight(netWeight.getWeight());
        settlementFormItemExportVO.setOuterboxGrossweight(CalcSpecificationUtil.calcSpecificationTotalGrossweight(List.of(specificationEntity)));
        settlementFormItemExportVO.setOuterboxGrossweightWeight(settlementFormItemExportVO.getOuterboxGrossweight().getWeight());
        settlementFormItemExportVO.setOuterboxNetweight(CalcSpecificationUtil.calcSpecificationTotalNetweight(List.of(specificationEntity)));
        settlementFormItemExportVO.setOuterboxNetweightWeight(settlementFormItemExportVO.getOuterboxNetweight().getWeight());
        settlementFormItemExportVO.setOuterboxHeight(specificationEntity.getOuterboxHeight());
        settlementFormItemExportVO.setOuterboxWidth(specificationEntity.getOuterboxWidth());
        settlementFormItemExportVO.setOuterboxLength(specificationEntity.getOuterboxLength());
        return settlementFormItemExportVO;
    }
    SettlementFormAddSubItemExportVO convertSettlementFormAddSubItemExportVO(AddSubItemDTO addSubItem);

    default List<SettlementFormAddSubItemExportVO> convertSettlementFormAddSubItemExportVOList(List<AddSubItemDTO> addSubItem) {
        return CollectionUtils.convertList(addSubItem, s -> {
            return convertSettlementFormAddSubItemExportVO(s);
        });
    }
}