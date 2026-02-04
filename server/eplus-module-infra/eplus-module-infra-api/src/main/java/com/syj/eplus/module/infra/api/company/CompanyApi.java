package com.syj.eplus.module.infra.api.company;

import com.syj.eplus.module.infra.api.company.dto.CompanyBankRespDTO;
import com.syj.eplus.module.infra.api.company.dto.SimpleCompanyDTO;

import java.util.List;
import java.util.Map;

public interface CompanyApi {
    /**
     * 获取法人单位精简列表
     *
     * @return 法人单位精简列表
     */
    Map<Long, SimpleCompanyDTO> getSimpleCompanyDTO();
    Map<String, SimpleCompanyDTO> getStringSimpleCompanyDTO(List<String>idList);
    SimpleCompanyDTO getCompanyDTO(Long id);

    /**
     * 获取可加工的公司列表
     * @return
     */
    List<SimpleCompanyDTO> listProducedCompany();

    /**
     * 校验公司是否存在内部客户及供应商
     * @param idList
     * @return
     */
    void checkCompanyCustAndVender(List<Long> idList);

    Map<Long, CompanyBankRespDTO> getCompanyBankDTOMap();

    Map<String, SimpleCompanyDTO> getSimpleCompanyDTOToName();
}
