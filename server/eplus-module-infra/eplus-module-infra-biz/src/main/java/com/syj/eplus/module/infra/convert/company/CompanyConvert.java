package com.syj.eplus.module.infra.convert.company;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.syj.eplus.framework.common.entity.DictSimpleFileList;
import com.syj.eplus.module.infra.api.company.dto.SimpleCompanyDTO;
import com.syj.eplus.module.infra.controller.admin.company.vo.CompanyRespVO;
import com.syj.eplus.module.infra.controller.admin.company.vo.CompanySaveReqVO;
import com.syj.eplus.module.infra.dal.dataobject.company.CompanyDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CompanyConvert {

    CompanyConvert INSTANCE = Mappers.getMapper(CompanyConvert.class);

    @Mapping(target = "picture", source = "picture", qualifiedByName = "pictureListToString")
    @Mapping(target = "companyBankRespDTO", ignore = true)
    SimpleCompanyDTO convertSimpleCompanyDTO(CompanyDO companyDO);

    List<SimpleCompanyDTO> convertSimpleCompanyDTOList(List<CompanyDO> companyDOList);

    @Mapping(target = "companyBankList", ignore = true)
    List<CompanyRespVO> convertCompanyRespVOList(List<CompanyDO> companyDOList);

    @Mapping(target = "companyBankList", ignore = true)
    CompanyRespVO convertCompanyRespVO(CompanyDO companyDO);

    CompanyDO convertCompanyDO(CompanySaveReqVO companySaveReqVO);

    /**
     * 将图片列表转换为 JSON 字符串
     */
    @Named("pictureListToString")
    default String pictureListToString(List<DictSimpleFileList> pictureList) {
        if (pictureList == null || pictureList.isEmpty()) {
            return null;
        }
        return JsonUtils.toJsonString(pictureList);
    }

}
