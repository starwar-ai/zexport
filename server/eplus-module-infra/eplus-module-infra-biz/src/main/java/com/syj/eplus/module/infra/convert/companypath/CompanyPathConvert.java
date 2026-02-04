package com.syj.eplus.module.infra.convert.companypath;

import com.syj.eplus.framework.common.entity.JsonCompanyPath;
import com.syj.eplus.module.infra.api.companypath.dto.CompanyPathDTO;
import com.syj.eplus.module.infra.controller.admin.companypath.vo.CompanyPathRespVO;
import com.syj.eplus.module.infra.controller.admin.companypath.vo.CompanyPathSaveReqVO;
import com.syj.eplus.module.infra.dal.dataobject.companypath.CompanyPathDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


@Mapper
public interface CompanyPathConvert {

    CompanyPathConvert INSTANCE = Mappers.getMapper(CompanyPathConvert.class);

    CompanyPathRespVO convert(CompanyPathDO pathDO);

    default CompanyPathRespVO convertCompanyPathRespVO(CompanyPathDO pathDO) {
        CompanyPathRespVO pathRespVO = convert(pathDO);
        return pathRespVO;
    }

    @Mapping(target = "path", ignore = true)
    CompanyPathDO convertCompanyPathDO(CompanyPathSaveReqVO saveReqVO);

    /**
     * JsonCompanyPath 转换为 CompanyPathDTO
     */
    CompanyPathDTO convertToDTO(JsonCompanyPath jsonCompanyPath);
}