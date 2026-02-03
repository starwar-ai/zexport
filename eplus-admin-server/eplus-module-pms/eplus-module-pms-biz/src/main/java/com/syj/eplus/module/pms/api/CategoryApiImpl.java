package com.syj.eplus.module.pms.api;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.syj.eplus.module.pms.api.Category.CategoryApi;
import com.syj.eplus.module.pms.api.Category.dto.SimpleCategoryDTO;
import com.syj.eplus.module.pms.dal.dataobject.category.CategoryDO;
import com.syj.eplus.module.pms.service.category.CategoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class CategoryApiImpl implements CategoryApi {

    @Resource
    private CategoryService categoryService;

    @Override
    public List<SimpleCategoryDTO> getListByIdList(List<Long> list) {
        List<CategoryDO> listByIdList = categoryService.getListByIdList(list);
        return BeanUtils.toBean(listByIdList,SimpleCategoryDTO.class);
    }
}
