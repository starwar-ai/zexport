package cn.iocoder.yudao.module.system.api.dict;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.system.api.dict.dto.DictDataRespDTO;
import cn.iocoder.yudao.module.system.api.dict.dto.DictTypeRespDTO;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.service.dict.DictTypeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class DictTypeApiImpl implements DictTypeApi {

    @Resource
    private DictTypeService dictTypeService;


    @Override
    public List<DictTypeRespDTO> getListByTypeList(List<String> typeList) {
        return dictTypeService.getListByTypeList(typeList);
    }

    @Override
    public List<DictDataRespDTO> getHsDataUnitList() {
        List<DictDataDO> hsDataUnitList = dictTypeService.getHsDataUnitList();
        if(CollUtil.isEmpty(hsDataUnitList)){
            return null;
        }
        return BeanUtils.toBean(hsDataUnitList,DictDataRespDTO.class);
    }
}
