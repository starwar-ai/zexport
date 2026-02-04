package com.syj.eplus.module.infra.api.company;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.syj.eplus.module.infra.api.company.dto.CompanyBankRespDTO;
import com.syj.eplus.module.infra.api.company.dto.SimpleCompanyDTO;
import com.syj.eplus.module.infra.controller.admin.company.vo.CompanyBankRespVO;
import com.syj.eplus.module.infra.dal.dataobject.company.CompanyDO;
import com.syj.eplus.module.infra.enums.company.CompanyNatureEnum;
import com.syj.eplus.module.infra.service.company.CompanyService;
import com.syj.eplus.module.crm.api.cust.CustApi;
import com.syj.eplus.module.crm.api.cust.dto.SimpleCustRespDTO;
import com.syj.eplus.module.scm.api.vender.VenderApi;
import com.syj.eplus.module.scm.api.vender.dto.SimpleVenderRespDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

@Service
public class CompanyApiImpl implements CompanyApi {

    @Resource
    private CompanyService companyService;
    @Resource
    private VenderApi venderApi;
    @Resource
    private CustApi custApi;


    @Override
    public Map<Long, SimpleCompanyDTO> getSimpleCompanyDTO() {
        List<SimpleCompanyDTO> simpleCompanyDTOList = companyService.getSimpleCompanyDTO();
        if (CollUtil.isEmpty(simpleCompanyDTOList)) {
            return Collections.emptyMap();
        }
        return simpleCompanyDTOList.stream().collect(Collectors.toMap(SimpleCompanyDTO::getId, simpleCompanyDTO -> simpleCompanyDTO));

    }

    @Override
    public Map<String, SimpleCompanyDTO> getStringSimpleCompanyDTO(List<String> idList) {
        List<SimpleCompanyDTO> simpleCompanyDTOList = companyService.getStringSimpleCompanyDTO(idList);
        if (CollUtil.isEmpty(simpleCompanyDTOList)) {
            return Collections.emptyMap();
        }
        return simpleCompanyDTOList.stream().collect(Collectors.toMap(
                simpleCompanyDTO -> String.valueOf(simpleCompanyDTO.getId()),
                simpleCompanyDTO -> simpleCompanyDTO,
                (e, r) -> e
        ));
    }

    @Override
    public SimpleCompanyDTO getCompanyDTO(Long id) {
        return companyService.getCompanyDTO(id);
    }

    @Override
    public List<SimpleCompanyDTO> listProducedCompany() {
        List<SimpleCompanyDTO> simpleCompanyDTOList = companyService.getSimpleCompanyDTO();
        if (CollUtil.isEmpty(simpleCompanyDTOList)) {
            return Collections.emptyList();
        }
        // 过滤有加工资质的公司
        List<SimpleCompanyDTO> processedList = simpleCompanyDTOList.stream().filter(x ->
                CompanyNatureEnum.FACTORY.getValue() == x.getCompanyNature().intValue()
        ).toList();
        return processedList;
    }

    @Override
    public void checkCompanyCustAndVender(List<Long> idList) {
        List<CompanyDO> companyDOList = companyService.listByIds(idList);
        if (!CollUtil.isEmpty(companyDOList)) {
            companyDOList.forEach(x->{
                Long companyId = x.getId();
                SimpleCustRespDTO simpleCust = custApi.getSimpleCustByInternalCompanyId(companyId);
                String errStr = "";
                if(ObjectUtil.isNull(simpleCust)){
                    errStr += x.getName() +" 不存在内部客户;";
                }
                SimpleVenderRespDTO simpleVender = venderApi.getSimpleVenderByInternalCompanyId(companyId);
                if(ObjectUtil.isNull(simpleVender)){
                    errStr += x.getName() +" 不存在内部供应商;";
                }
                if (StringUtils.isNotBlank(errStr)){
                    throw  exception(new ErrorCode(1_002_035_001, errStr));
                }
            });
        }
    }

    @Override
    public Map<Long, CompanyBankRespDTO> getCompanyBankDTOMap() {

        List<CompanyBankRespVO> companyBankList = companyService.getCompanyBankList();
        if(CollUtil.isEmpty(companyBankList)){
            return null;
        }
        List<CompanyBankRespDTO> dtoList = BeanUtils.toBean(companyBankList, CompanyBankRespDTO.class);
        return dtoList.stream().collect(Collectors.toMap(CompanyBankRespDTO::getId,s->s,(s1,s2)->s2));
    }

    @Override
    public Map<String, SimpleCompanyDTO> getSimpleCompanyDTOToName() {
        List<SimpleCompanyDTO> simpleCompanyDTOList = companyService.getAllList();
        if (CollUtil.isEmpty(simpleCompanyDTOList)) {
            return Collections.emptyMap();
        }
        return simpleCompanyDTOList.stream().collect(Collectors.toMap(
                SimpleCompanyDTO::getName,
                simpleCompanyDTO -> simpleCompanyDTO,
                (e, r) -> e
        ));
    }


}
