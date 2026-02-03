package cn.iocoder.yudao.module.system.api.dict;

import cn.iocoder.yudao.module.system.api.dict.dto.DictDataRespDTO;
import cn.iocoder.yudao.module.system.api.dict.dto.DictTypeRespDTO;

import java.util.List;


public interface DictTypeApi {

    List<DictTypeRespDTO> getListByTypeList(List<String> typeList);

    List<DictDataRespDTO> getHsDataUnitList();

}
