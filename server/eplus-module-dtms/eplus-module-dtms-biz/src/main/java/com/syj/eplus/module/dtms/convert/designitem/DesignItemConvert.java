package com.syj.eplus.module.dtms.convert.designitem;

import com.syj.eplus.module.dtms.controller.admin.designitem.vo.DesignItemRespVO;
import com.syj.eplus.module.dtms.controller.admin.designitem.vo.DesignItemSaveReqVO;
import com.syj.eplus.module.dtms.dal.dataobject.designitem.DesignItemDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface DesignItemConvert {

        DesignItemConvert INSTANCE = Mappers.getMapper(DesignItemConvert.class);

        DesignItemRespVO convert(DesignItemDO designItemDO);

        default DesignItemRespVO convertDesignItemRespVO(DesignItemDO designItemDO){
                DesignItemRespVO designItemRespVO = convert(designItemDO);
                return designItemRespVO;
        }

    DesignItemDO convertDesignItemDO(DesignItemSaveReqVO saveReqVO);
}
