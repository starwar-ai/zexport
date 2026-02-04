package com.syj.eplus.module.pms.service.convent;

/**
 * @Description：
 * @Author：du
 * @Date：2024/2/7 16:49
 */

import com.syj.eplus.module.pms.controller.admin.spu.vo.SpuInfoSaveReqVO;
import com.syj.eplus.module.pms.controller.admin.spu.vo.SpuRespVO;
import com.syj.eplus.module.pms.dal.dataobject.spu.SpuDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SpuConvent {
    SpuConvent INSTANCE = Mappers.getMapper(SpuConvent.class);


    SpuRespVO convert(SpuDO spuDO);

    SpuInfoSaveReqVO convertInfoResp(SpuDO spuDO);

}
