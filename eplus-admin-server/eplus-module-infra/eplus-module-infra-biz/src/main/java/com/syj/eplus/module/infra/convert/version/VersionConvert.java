package com.syj.eplus.module.infra.convert.version;

import com.syj.eplus.module.infra.controller.admin.version.vo.VersionRespVO;
import com.syj.eplus.module.infra.controller.admin.version.vo.VersionSaveReqVO;
import com.syj.eplus.module.infra.dal.dataobject.version.VersionDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface VersionConvert {

        VersionConvert INSTANCE = Mappers.getMapper(VersionConvert.class);

        VersionRespVO convert(VersionDO versionDO);

        default VersionRespVO convertVersionRespVO(VersionDO versionDO){
                return convert(versionDO);
        }

    VersionDO convertVersionDO(VersionSaveReqVO saveReqVO);
}