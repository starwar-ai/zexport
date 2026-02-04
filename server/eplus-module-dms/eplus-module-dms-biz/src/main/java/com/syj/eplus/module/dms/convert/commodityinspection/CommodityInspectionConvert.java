package com.syj.eplus.module.dms.convert.commodityinspection;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import com.syj.eplus.framework.common.enums.UnitPreOuterBoxTypeEnum;
import com.syj.eplus.framework.common.util.NumUtil;
import com.syj.eplus.module.dms.controller.admin.commodityinspection.vo.CommodityInspectionItemExportVO;
import com.syj.eplus.module.dms.controller.admin.commodityinspection.vo.CommodityInspectionRespVO;
import com.syj.eplus.module.dms.controller.admin.commodityinspection.vo.CommodityInspectionSaveReqVO;
import com.syj.eplus.module.dms.dal.dataobject.commodityinspection.CommodityInspectionDO;
import com.syj.eplus.module.dms.dal.dataobject.commodityinspectionitem.CommodityInspectionItem;
import com.syj.eplus.module.pms.api.packageType.dto.PackageTypeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;


@Mapper
public interface CommodityInspectionConvert {

        CommodityInspectionConvert INSTANCE = Mappers.getMapper(CommodityInspectionConvert.class);

        CommodityInspectionRespVO convert(CommodityInspectionDO commodityInspectionDO);

        default CommodityInspectionRespVO convertCommodityInspectionRespVO(CommodityInspectionDO commodityInspectionDO){
                CommodityInspectionRespVO commodityInspectionRespVO = convert(commodityInspectionDO);
                return commodityInspectionRespVO;
        }

    default List<CommodityInspectionRespVO> convertCommodityInspectionRespVO(List<CommodityInspectionDO> commodityInspectionDOList, Map<Long, List<CommodityInspectionItem>> commodityInspectionItemMap, List<PackageTypeDTO> packageTypeList ) {
                return CollectionUtils.convertList(commodityInspectionDOList, commodityInspectionDO -> {
                        Long  commodityInspectionId = commodityInspectionDO.getId();
                        CommodityInspectionRespVO commodityInspectionRespVO = convert(commodityInspectionDO);
                        if (CollUtil.isNotEmpty(commodityInspectionItemMap)) {
                                List<CommodityInspectionItem> commodityInspectionItemList = commodityInspectionItemMap.get(commodityInspectionId);
                                if (CollUtil.isNotEmpty(commodityInspectionItemList)) {
                                        commodityInspectionItemList.forEach(s->{
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
                                        commodityInspectionRespVO.setChildren(commodityInspectionItemList);
                                }
                        }

                        return commodityInspectionRespVO;
                });
        }

        CommodityInspectionDO convertCommodityInspectionDO(CommodityInspectionSaveReqVO saveReqVO);

        CommodityInspectionItemExportVO convertCommodityInspectionItemExportVO(CommodityInspectionItem commodityInspectionItem);
        default List<CommodityInspectionItemExportVO> convertDeclarationItemExportVOList(List<CommodityInspectionItem> commodityInspectionItem) {
                return CollectionUtils.convertList(commodityInspectionItem, s -> {
                        CommodityInspectionItemExportVO commodityInspectionItemExportVO =  convertCommodityInspectionItemExportVO(s);
                        if(s.getPurchaseTotalQuantity()!=null && s.getPurchaseWithTaxPrice().getAmount()!=null){
                                commodityInspectionItemExportVO.setPurchaseTotalAmountAmount(NumUtil.mul(s.getPurchaseTotalQuantity(), s.getPurchaseWithTaxPrice().getAmount()));
                        }
                        if(s.getUnitPerOuterbox()!=null){
                                commodityInspectionItemExportVO.setUnitPerOuterbox(UnitPreOuterBoxTypeEnum.getNameByValue(s.getUnitPerOuterbox()));
                        }
                        return commodityInspectionItemExportVO;
                });
        }
}