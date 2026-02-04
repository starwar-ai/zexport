package com.syj.eplus.module.pms.api.Category;


import com.syj.eplus.module.pms.api.Category.dto.SimpleCategoryDTO;

import java.util.List;

public interface CategoryApi {

    List<SimpleCategoryDTO> getListByIdList(List<Long> list);
}
