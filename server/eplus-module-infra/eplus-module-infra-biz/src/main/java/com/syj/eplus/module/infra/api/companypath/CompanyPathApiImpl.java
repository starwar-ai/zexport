package com.syj.eplus.module.infra.api.companypath;

import com.syj.eplus.module.infra.api.companypath.dto.CompanyPathDTO;
import com.syj.eplus.module.infra.service.companypath.CompanyPathService;
import com.syj.eplus.framework.common.entity.JsonCompanyPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/4 17:30
 */
@Service
public class CompanyPathApiImpl implements CompanyPathApi{

    @Lazy
    @Autowired
    private CompanyPathService companyPathService;

    @Override
    public Map<Long, CompanyPathDTO> getCompanyPathMap(List<Long> idList) {
        return companyPathService.getCompanyPathMap(idList);
    }
}
