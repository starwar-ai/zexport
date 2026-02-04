package com.syj.eplus.module.pms.convert.packagetype;

import com.syj.eplus.module.pms.controller.admin.packagetype.vo.PackageTypeRespVO;
import com.syj.eplus.module.pms.controller.admin.packagetype.vo.PackageTypeSaveReqVO;
import com.syj.eplus.module.pms.dal.dataobject.packagetype.PackageTypeDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface PackageTypeConvert {

        PackageTypeConvert INSTANCE = Mappers.getMapper(PackageTypeConvert.class);

        PackageTypeRespVO convert(PackageTypeDO packageTypeDO);

        default PackageTypeRespVO convertPackageTypeRespVO(PackageTypeDO packageTypeDO){
                PackageTypeRespVO packageTypeRespVO = convert(packageTypeDO);
                return packageTypeRespVO;
        }

    PackageTypeDO convertPackageTypeDO(PackageTypeSaveReqVO saveReqVO);
}