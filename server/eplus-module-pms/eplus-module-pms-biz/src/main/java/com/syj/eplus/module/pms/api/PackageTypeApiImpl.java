package com.syj.eplus.module.pms.api;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.syj.eplus.module.pms.api.packageType.PackageTypeApi;
import com.syj.eplus.module.pms.api.packageType.dto.PackageTypeDTO;
import com.syj.eplus.module.pms.controller.admin.packagetype.vo.PackageTypeSimplePageReqVO;
import com.syj.eplus.module.pms.dal.dataobject.packagetype.PackageTypeDO;
import com.syj.eplus.module.pms.service.packagetype.PackageTypeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class PackageTypeApiImpl implements PackageTypeApi {

    @Resource
    private PackageTypeService packageTypeService;

    @Override
    public List<PackageTypeDTO> getList() {
        List<PackageTypeDO> list = packageTypeService.getList(new PackageTypeSimplePageReqVO());
        return BeanUtils.toBean(list,PackageTypeDTO.class);
    }

    @Override
    public PackageTypeDTO getById(Long id) {
        PackageTypeDO packageTypeDO = packageTypeService.getById(id);
        return BeanUtils.toBean(packageTypeDO,PackageTypeDTO.class);
    }

    @Override
    public Map<Long, PackageTypeDTO> getByIdList(List<Long> packageIdList) {
        List<PackageTypeDO> doList = packageTypeService.getByIdList(packageIdList);
        if(CollUtil.isEmpty(doList)){
            return null;
        }
        List<PackageTypeDTO> typeDTOList = BeanUtils.toBean(doList, PackageTypeDTO.class);
        return typeDTOList.stream().collect(Collectors.toMap(PackageTypeDTO::getId,s->s,(s1,s2)->s1));
    }

    @Override
    public Map<Long, String> getAllNameCache() {
        return packageTypeService.getAllNameCache();
    }
}
