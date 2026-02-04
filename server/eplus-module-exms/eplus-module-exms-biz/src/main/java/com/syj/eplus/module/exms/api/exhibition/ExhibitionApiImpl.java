package com.syj.eplus.module.exms.api.exhibition;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.syj.eplus.module.exms.api.exhibition.dto.ExhibitionDTO;
import com.syj.eplus.module.exms.dal.dataobject.eventcategory.EventCategoryDO;
import com.syj.eplus.module.exms.dal.dataobject.exhibition.ExhibitionDO;
import com.syj.eplus.module.exms.dal.mysql.eventcategory.EventCategoryMapper;
import com.syj.eplus.module.exms.dal.mysql.exhibition.ExhibitionMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

@Service
public class ExhibitionApiImpl implements ExhibitionApi{
    @Resource
    private ExhibitionMapper exhibitionMapper;

    @Resource
    private EventCategoryMapper eventCategoryMapper;


    @Override
    public ExhibitionDTO getExhibitionById(Long exhibitionId, Long eventCategoryId) {
        if (Objects.isNull(exhibitionId)&&Objects.isNull(eventCategoryId)){
            return null;
        }
        ExhibitionDTO exhibitionDTO = new ExhibitionDTO();
        if (Objects.nonNull(exhibitionId)){
            ExhibitionDO exhibitionDO = exhibitionMapper.selectById(exhibitionId);
            if (Objects.nonNull(exhibitionDO)){
                exhibitionDTO.setExhibitionId(exhibitionDO.getId());
                exhibitionDTO.setExhibitionName(exhibitionDO.getName());
            }
        }
        if (Objects.nonNull(eventCategoryId)){
            EventCategoryDO eventCategoryDO = eventCategoryMapper.selectById(eventCategoryId);
            if (Objects.nonNull(eventCategoryDO)){
                exhibitionDTO.setExmsEventCategoryId(eventCategoryDO.getId());
                exhibitionDTO.setExmsEventCategoryName(eventCategoryDO.getName());
            }
        }
        return exhibitionDTO;
    }

    @Override
    public ExhibitionDTO getExhibitionById(Long exhibitionId) {
        if (Objects.isNull(exhibitionId)){
            return null;
        }
        ExhibitionDO exhibitionDO = exhibitionMapper.selectById(exhibitionId);
        ExhibitionDTO exhibitionDTO = BeanUtils.toBean(exhibitionDO,ExhibitionDTO.class);
        if (Objects.nonNull(exhibitionDO)){
            exhibitionDTO.setExhibitionId(exhibitionDO.getId());
            exhibitionDTO.setExhibitionName(exhibitionDO.getName());
        }
        return  exhibitionDTO;
    }
}
