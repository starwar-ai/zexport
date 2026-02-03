package com.syj.eplus.module.dms.convert.shipmentplan;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import com.syj.eplus.framework.common.dict.CalculationDict;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.framework.common.enums.UnitPreOuterBoxTypeEnum;
import com.syj.eplus.framework.common.util.JsonWeightUtil;
import com.syj.eplus.framework.common.util.VolumeUtil;
import com.syj.eplus.module.dms.controller.admin.shipmentplan.vo.ShipmentPlanItemExportVO;
import com.syj.eplus.module.dms.controller.admin.shipmentplan.vo.ShipmentPlanRespVO;
import com.syj.eplus.module.dms.controller.admin.shipmentplan.vo.ShipmentPlanSaveReqVO;
import com.syj.eplus.module.dms.dal.dataobject.shipmentplan.ShipmentPlanDO;
import com.syj.eplus.module.dms.dal.dataobject.shipmentplanitem.ShipmentPlanItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.bpm.enums.ErrorCodeConstants.SHIPMENT_ITEM_SPEC_IS_NULL;


@Mapper
public interface ShipmentPlanConvert {

        ShipmentPlanConvert INSTANCE = Mappers.getMapper(ShipmentPlanConvert.class);

        ShipmentPlanRespVO convert(ShipmentPlanDO shipmentPlanDO);

        default ShipmentPlanRespVO convertShipmentPlanRespVO(ShipmentPlanDO shipmentPlanDO){
                ShipmentPlanRespVO shipmentPlanRespVO = convert(shipmentPlanDO);
                return shipmentPlanRespVO;
        }

    ShipmentPlanDO convertShipmentPlanDO(ShipmentPlanSaveReqVO saveReqVO);

        @Mapping(target = "totalGrossweight", ignore = true)
        @Mapping(target = "outerboxNetweight", ignore = true)
        @Mapping(target = "totalNetweight", ignore = true)
        @Mapping(target = "manager", ignore = true)
        ShipmentPlanItemExportVO convertShipmentPlanItemExportVO(ShipmentPlanItem shipmentPlanItem);
        default List<ShipmentPlanItemExportVO> convertShipmentPlanItemExportVO(List<ShipmentPlanItem> shipmentPlanItemList, ShipmentPlanDO shipmentPlanDO){
                if (CollUtil.isEmpty(shipmentPlanItemList)){
                        return List.of();
                }
                return shipmentPlanItemList.stream().map(shipmentPlanItem -> {
                        List<JsonSpecificationEntity> specificationList = shipmentPlanItem.getSpecificationList();
                        if (CollUtil.isEmpty(specificationList)){
                                throw exception(SHIPMENT_ITEM_SPEC_IS_NULL);
                        }
                        return specificationList.stream().map(s->{
                                ShipmentPlanItemExportVO shipmentPlanItemExportVO = convertShipmentPlanItemExportVO(shipmentPlanItem);
                                shipmentPlanItemExportVO.setName(shipmentPlanItem.getName());
                                if(shipmentPlanItem.getBoxCount()!=null){
                                        shipmentPlanItemExportVO.setBoxCountLabel(shipmentPlanItem.getBoxCount() + UnitPreOuterBoxTypeEnum.getNameByValue(shipmentPlanItem.getUnitPerOuterbox()));
                                }
                                //外箱毛重
                                BigDecimal outerboxGrossweight = BigDecimal.ZERO;
                                JsonWeight jsonOuterboxGrossweight = s.getOuterboxGrossweight();
                                if (Objects.nonNull(jsonOuterboxGrossweight)){
                                        outerboxGrossweight = Objects.isNull(jsonOuterboxGrossweight.getWeight())?BigDecimal.ZERO: JsonWeightUtil.convertToKg(jsonOuterboxGrossweight).getWeight();
                                }
                                shipmentPlanItemExportVO.setOuterboxGrossweight(outerboxGrossweight.setScale(CalculationDict.TWO, RoundingMode.HALF_UP).stripTrailingZeros());
                                //总毛重
                                BigDecimal totalGrossweight = BigDecimal.ZERO;
                                if (Objects.nonNull(jsonOuterboxGrossweight)){
                                        totalGrossweight = Objects.isNull(jsonOuterboxGrossweight.getWeight())?BigDecimal.ZERO: JsonWeightUtil.convertToKg(jsonOuterboxGrossweight).getWeight().multiply(BigDecimal.valueOf(shipmentPlanItem.getBoxCount()));
                                }
                                shipmentPlanItemExportVO.setTotalGrossweight(totalGrossweight.setScale(CalculationDict.TWO, RoundingMode.HALF_UP).stripTrailingZeros());
                                //单箱毛重
                                if(shipmentPlanItem.getBoxCount()>0){
                                        shipmentPlanItemExportVO.setSingleGrossweight(NumberUtil.div(totalGrossweight,shipmentPlanItem.getBoxCount()).setScale(CalculationDict.TWO, RoundingMode.HALF_UP).stripTrailingZeros());
                                }
                                //外箱净重
                                BigDecimal outerboxNetweight = BigDecimal.ZERO;
                                JsonWeight jsonOuterboxNetweight = s.getOuterboxNetweight();
                                if (Objects.nonNull(jsonOuterboxNetweight)){
                                        outerboxNetweight = Objects.isNull(jsonOuterboxNetweight.getWeight())?BigDecimal.ZERO:JsonWeightUtil.convertToKg(jsonOuterboxNetweight).getWeight();
                                }
                                shipmentPlanItemExportVO.setOuterboxNetweight(outerboxNetweight.setScale(CalculationDict.TWO, RoundingMode.HALF_UP).stripTrailingZeros());
                                //总净重
                                BigDecimal totalNetweight = BigDecimal.ZERO;
                                if (Objects.nonNull(jsonOuterboxNetweight)){
                                        totalNetweight = Objects.isNull(jsonOuterboxNetweight.getWeight())?BigDecimal.ZERO:JsonWeightUtil.convertToKg(jsonOuterboxNetweight).getWeight().multiply(BigDecimal.valueOf(shipmentPlanItem.getBoxCount()));
                                }
                                //单箱净重
                                if(shipmentPlanItem.getBoxCount()>0){
                                        shipmentPlanItemExportVO.setSingleNetweight(NumberUtil.div(totalNetweight,shipmentPlanItem.getBoxCount()).setScale(CalculationDict.TWO, RoundingMode.HALF_UP).stripTrailingZeros());
                                }
                                shipmentPlanItemExportVO.setTotalNetweight(totalNetweight.setScale(CalculationDict.TWO, RoundingMode.HALF_UP).stripTrailingZeros());
                                shipmentPlanItemExportVO.setTotalVolume(VolumeUtil.processVolume(shipmentPlanItem.getTotalVolume()));
                                shipmentPlanItemExportVO.setOuterboxHeight(s.getOuterboxHeight().setScale(2, RoundingMode.HALF_UP).stripTrailingZeros());
                                shipmentPlanItemExportVO.setOuterboxWidth(s.getOuterboxWidth().setScale(2, RoundingMode.HALF_UP).stripTrailingZeros());
                                shipmentPlanItemExportVO.setOuterboxLength(s.getOuterboxLength().setScale(2, RoundingMode.HALF_UP).stripTrailingZeros());
                                shipmentPlanItemExportVO.setOuterboxVolume(VolumeUtil.processVolume(s.getOuterboxVolume()));
                                if(Objects.nonNull(shipmentPlanItem.getManager())){
                                        shipmentPlanItemExportVO.setManager(shipmentPlanItem.getManager().getNickname());
                                }
                                shipmentPlanItemExportVO.setSpec( s.getOuterboxLength().setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString() + "*" +
                                        s.getOuterboxWidth().setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString() + "*" +
                                        s.getOuterboxHeight().setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());
                                return shipmentPlanItemExportVO;
                        }).toList();
                }).flatMap(List::stream).toList();
        }
}