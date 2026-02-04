package com.syj.eplus.module.infra.converter;

import com.syj.eplus.module.infra.api.sn.dto.SnDTO;
import com.syj.eplus.module.infra.controller.admin.sn.vo.SnSaveReqVO;
import com.syj.eplus.module.infra.dal.dataobject.sn.SnDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SnConverter {

    SnConverter INSTANCE = Mappers.getMapper(SnConverter.class);

    /**
     * 将 SnDO 转换为 SnSaveReqVO
     */
    SnSaveReqVO convertToSaveReqVO(SnDO snDO);

    /**
     * 将 SnDO 转换为 SnDTO
     */
    SnDTO convertToDTO(SnDO snDO);

    /**
     * 将 SnSaveReqVO 转换为 SnDO
     */
    SnDO convert(SnSaveReqVO saveReqVO);
}