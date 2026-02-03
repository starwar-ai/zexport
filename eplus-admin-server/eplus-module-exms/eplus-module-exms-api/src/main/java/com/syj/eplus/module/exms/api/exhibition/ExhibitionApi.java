package com.syj.eplus.module.exms.api.exhibition;

import com.syj.eplus.module.exms.api.exhibition.dto.ExhibitionDTO;

public interface ExhibitionApi {

    /**
     * 获取展会信息
     * @param exhibitionId 展会ID
     * @param eventCategoryId 展会系列id
     * @return 展会信息
     */
    ExhibitionDTO getExhibitionById(Long exhibitionId,Long eventCategoryId);

    ExhibitionDTO getExhibitionById(Long exhibitionId);

}
