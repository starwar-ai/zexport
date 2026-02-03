package com.syj.eplus.module.exms.convert.eventcategory;

import com.syj.eplus.module.exms.controller.admin.eventcategory.vo.EventCategoryRespVO;
import com.syj.eplus.module.exms.controller.admin.eventcategory.vo.EventCategorySaveReqVO;
import com.syj.eplus.module.exms.dal.dataobject.eventcategory.EventCategoryDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface EventCategoryConvert {
        EventCategoryConvert INSTANCE = Mappers.getMapper(EventCategoryConvert.class);
        EventCategoryRespVO convert(EventCategoryDO eventCategoryDO);
        default EventCategoryRespVO convertEventCategoryRespVO(EventCategoryDO eventCategoryDO){
                EventCategoryRespVO eventCategoryRespVO = convert(eventCategoryDO);
                return eventCategoryRespVO;
        }

    EventCategoryDO convertEventCategoryDO(EventCategorySaveReqVO saveReqVO);
}