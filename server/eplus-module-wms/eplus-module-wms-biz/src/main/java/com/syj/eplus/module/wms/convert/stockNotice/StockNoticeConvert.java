package com.syj.eplus.module.wms.convert.stockNotice;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import com.syj.eplus.framework.common.entity.JsonSpecificationEntity;
import com.syj.eplus.framework.common.entity.JsonWeight;
import com.syj.eplus.module.wms.api.stockNotice.dto.StockNoticePageReqDTO;
import com.syj.eplus.module.wms.api.stockNotice.dto.StockNoticeRespDTO;
import com.syj.eplus.module.wms.api.stockNotice.dto.StockNoticeSaveReqDTO;
import com.syj.eplus.module.wms.controller.admin.stockNotice.vo.StockNoticeItemExportVO;
import com.syj.eplus.module.wms.controller.admin.stockNotice.vo.StockNoticePageReqVO;
import com.syj.eplus.module.wms.controller.admin.stockNotice.vo.StockNoticeRespVO;
import com.syj.eplus.module.wms.controller.admin.stockNotice.vo.StockNoticeSaveReqVO;
import com.syj.eplus.module.wms.controller.admin.stockNoticeItem.vo.StockNoticeItemRespVO;
import com.syj.eplus.module.wms.dal.dataobject.stockNotice.StockNoticeDO;
import com.syj.eplus.module.wms.dal.dataobject.stockNotice.StockNoticeItemDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Mapper
public interface StockNoticeConvert {

        StockNoticeConvert INSTANCE = Mappers.getMapper(StockNoticeConvert.class);

        StockNoticeRespVO convert(StockNoticeDO stockNoticeDO);

        default StockNoticeRespVO convertNoticeRespVO(StockNoticeDO stockNoticeDO){
                StockNoticeRespVO stockNoticeRespVO = convert(stockNoticeDO);
                return stockNoticeRespVO;
        }

    StockNoticeDO convertNoticeDO(StockNoticeSaveReqVO saveReqVO);

        StockNoticeSaveReqVO convertNoticeSaveReqVO(StockNoticeSaveReqDTO stockNoticeSaveReqDTO);

        StockNoticePageReqVO convertNoticePageReqVO(StockNoticePageReqDTO stockNoticePageReqDTO);


        StockNoticeRespDTO convertNoticeRespDTO(StockNoticeRespVO stockNoticeRespVO);
        default List<StockNoticeRespDTO> convertStockNoticeRespDTOList(List<StockNoticeRespVO> stockNoticeRespVOList){
                return CollectionUtils.convertList(stockNoticeRespVOList, s->{
                        return convertNoticeRespDTO(s);
                });
        }

    List<StockNoticeItemRespVO> convertStockNoticeItemByDO(List<StockNoticeItemDO> stockNoticeItemDOList);


        StockNoticeItemExportVO convertStockNoticeItemExportVO(StockNoticeItemDO stockNoticeItemDO);
        default List<StockNoticeItemExportVO> convertDeclarationItemExportVOList(List<StockNoticeItemDO> stockNoticeItemDOList) {
               if (CollUtil.isEmpty(stockNoticeItemDOList)){
                       return List.of();
               }
               Map<Long, List<StockNoticeItemDO>> groupByShipmentItemId = stockNoticeItemDOList.stream()
                       .filter(s -> s.getShipmentItemId() != null)
                       .collect(java.util.stream.Collectors.groupingBy(StockNoticeItemDO::getShipmentItemId));
               List<StockNoticeItemExportVO> result = new ArrayList<>();
               
               groupByShipmentItemId.forEach((shipmentItemId, itemList) -> {
                       StockNoticeItemDO firstItem = itemList.get(0);
                       StockNoticeItemExportVO exportVO = convertStockNoticeItemExportVO(firstItem);
                       // 对需要求和的字段进行聚合：数量、箱数、体积、总毛重
                       Integer totalQuantity = itemList.stream()
                               .map(StockNoticeItemDO::getOrderQuantity)
                               .filter(java.util.Objects::nonNull)
                               .reduce(0, Integer::sum);
                       
                       Integer totalBoxQuantity = itemList.stream()
                               .map(StockNoticeItemDO::getOrderBoxQuantity)
                               .filter(java.util.Objects::nonNull)
                               .reduce(0, Integer::sum);
                       
                       BigDecimal totalVolume = itemList.stream()
                               .map(StockNoticeItemDO::getTotalVolume)
                               .filter(java.util.Objects::nonNull)
                               .reduce(BigDecimal.ZERO, BigDecimal::add);
                       
                       // 总毛重求和
                       BigDecimal totalWeight = itemList.stream()
                               .map(StockNoticeItemDO::getTotalWeight)
                               .filter(java.util.Objects::nonNull)
                               .map(JsonWeight::getWeight)
                               .filter(java.util.Objects::nonNull)
                               .reduce(BigDecimal.ZERO, BigDecimal::add);
                       
                       // 设置聚合后的值
                       exportVO.setOrderQuantity(String.valueOf(totalQuantity));
                       exportVO.setOrderBoxQuantity(totalBoxQuantity);
                       exportVO.setTotalVolume(totalVolume);
                       
                       JsonWeight totalWeightObj = new JsonWeight();
                       totalWeightObj.setWeight(totalWeight);
                       totalWeightObj.setUnit(firstItem.getTotalWeight().getUnit());
                       exportVO.setTotalWeight(totalWeightObj);
                       exportVO.setTotalWeightWeight(totalWeight);
                       
                       // 保留第一条的规格信息
                       List<JsonSpecificationEntity> specificationList = firstItem.getSpecificationList();
                       if (CollUtil.isNotEmpty(specificationList)) {
                               JsonSpecificationEntity specificationEntity = specificationList.get(0);
                               exportVO.setOuterboxGrossweight(specificationEntity.getOuterboxGrossweight());
                               if (specificationEntity.getOuterboxGrossweight() != null) {
                                       exportVO.setOuterboxGrossweightweight(specificationEntity.getOuterboxGrossweight().getWeight());
                               }
                               exportVO.setOuterboxHeight(specificationEntity.getOuterboxHeight());
                               exportVO.setOuterboxWidth(specificationEntity.getOuterboxWidth());
                               exportVO.setOuterboxLength(specificationEntity.getOuterboxLength());
                       }             
                       result.add(exportVO);
                     });      
               return result;
        }

}
