package com.syj.eplus.module.pms.api.packageType;


import com.syj.eplus.module.pms.api.packageType.dto.PackageTypeDTO;

import java.util.List;
import java.util.Map;

public interface PackageTypeApi {

    List<PackageTypeDTO> getList();

    PackageTypeDTO getById(Long id);

    Map<Long, PackageTypeDTO> getByIdList(List<Long> packageIdList);

    /**
     * 获取所有包装名称缓存
     * @return
     */
    Map<Long,String> getAllNameCache();
}
