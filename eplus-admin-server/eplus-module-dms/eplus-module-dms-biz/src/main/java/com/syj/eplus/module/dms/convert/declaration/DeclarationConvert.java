package com.syj.eplus.module.dms.convert.declaration;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import com.syj.eplus.module.dms.controller.admin.declaration.vo.DeclarationItemExportVO;
import com.syj.eplus.module.dms.controller.admin.declaration.vo.DeclarationRespVO;
import com.syj.eplus.module.dms.controller.admin.declaration.vo.DeclarationSaveReqVO;
import com.syj.eplus.module.dms.dal.dataobject.declaration.DeclarationDO;
import com.syj.eplus.module.dms.dal.dataobject.declarationitem.DeclarationItem;
import com.syj.eplus.module.pms.api.packageType.dto.PackageTypeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;


@Mapper
public interface DeclarationConvert {

        DeclarationConvert INSTANCE = Mappers.getMapper(DeclarationConvert.class);

        DeclarationRespVO convert(DeclarationDO declarationDO);

        default DeclarationRespVO convertDeclarationRespVO(DeclarationDO declarationDO){
                DeclarationRespVO declarationRespVO = convert(declarationDO);
                return declarationRespVO;
        }

    default List<DeclarationRespVO> convertDeclarationRespVO(List<DeclarationDO> declarationDOList, Map<Long, List<DeclarationItem>> declarationItemMap,List<PackageTypeDTO> packageTypeList) {
                return CollectionUtils.convertList(declarationDOList, declarationDO -> {
                        Long  declarationId = declarationDO.getId();
                        DeclarationRespVO declarationItemRespVO = convert(declarationDO);
                        if (CollUtil.isNotEmpty(declarationItemMap)) {
                                List<DeclarationItem> declarationItemList = declarationItemMap.get(declarationId);
                                if (CollUtil.isNotEmpty(declarationItemList)) {
                                        declarationItemList.forEach(s->{
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
                                        declarationItemRespVO.setChildren(declarationItemList);
                                }
                        }
                        return declarationItemRespVO;
                });
        }


        DeclarationDO convertDeclarationDO(DeclarationSaveReqVO saveReqVO);

        DeclarationItemExportVO convertDeclarationItemExportVO(DeclarationItem declarationItem);
        default List<DeclarationItemExportVO> convertDeclarationItemExportVOList(List<DeclarationItem> declarationItemList) {
                return CollectionUtils.convertList(declarationItemList, s -> {
                        return convertDeclarationItemExportVO(s);
                });
        }
}