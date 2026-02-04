package com.syj.eplus.module.dtms.convert.design;

import com.syj.eplus.module.dtms.controller.admin.design.vo.DesignRespVO;
import com.syj.eplus.module.dtms.controller.admin.design.vo.DesignSaveReqVO;
import com.syj.eplus.module.dtms.dal.dataobject.design.DesignDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface DesignConvert {

        DesignConvert INSTANCE = Mappers.getMapper(DesignConvert.class);

        DesignRespVO convert(DesignDO designDO);

        default DesignRespVO convertDesignRespVO(DesignDO designDO){
                DesignRespVO designRespVO = convert(designDO);
                return designRespVO;
        }

    DesignDO convertDesignDO(DesignSaveReqVO saveReqVO);
}
