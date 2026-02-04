package com.syj.eplus.module.infra.api.companypath;

import com.syj.eplus.module.infra.api.companypath.dto.CompanyPathDTO;

import java.util.List;
import java.util.Map;

/**
 * @Description：
 * @Author：du
 * @Date：2024/7/4 17:25
 */
public interface CompanyPathApi {

    /**
     * 根据路径id获取路径缓存
     * @param idList 路径id列表
     * @return 路径缓存
     */
     Map<Long,CompanyPathDTO> getCompanyPathMap(List<Long>idList);
}
