package com.syj.eplus.module.crm.api.category;

import java.util.List;
import java.util.Map;

/**
 * 客户分类 API 接口
 *
 * @author 波波
 */
public interface CrmCategoryApi {

    /**
     * 根据分类ID列表获取分类名称映射
     *
     * @param categoryIds 分类ID列表
     * @return 分类ID到名称的映射
     */
    Map<Long, String> getCategoryNameMap(List<Long> categoryIds);

}
