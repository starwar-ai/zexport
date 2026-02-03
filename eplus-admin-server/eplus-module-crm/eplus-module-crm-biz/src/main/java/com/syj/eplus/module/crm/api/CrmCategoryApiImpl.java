package com.syj.eplus.module.crm.api;

import com.syj.eplus.module.crm.api.category.CrmCategoryApi;
import com.syj.eplus.module.crm.service.category.CrmCategoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 客户分类 API 实现类
 *
 * @author 波波
 */
@Service
public class CrmCategoryApiImpl implements CrmCategoryApi {

    @Resource
    private CrmCategoryService crmCategoryService;

    @Override
    public Map<Long, String> getCategoryNameMap(List<Long> categoryIds) {
        return crmCategoryService.getCategroyNameMap(categoryIds);
    }

}
