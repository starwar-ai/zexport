package com.syj.eplus.module.exms.convert.exhibition;

import com.syj.eplus.module.exms.controller.admin.exhibition.vo.ExhibitionRespVO;
import com.syj.eplus.module.exms.controller.admin.exhibition.vo.ExhibitionSaveReqVO;
import com.syj.eplus.module.exms.dal.dataobject.exhibition.ExhibitionDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface ExhibitionConvert {

        ExhibitionConvert INSTANCE = Mappers.getMapper(ExhibitionConvert.class);

        ExhibitionRespVO convert(ExhibitionDO exhibitionDO);

        default ExhibitionRespVO convertExhibitionRespVO(ExhibitionDO exhibitionDO){
                ExhibitionRespVO exhibitionRespVO = convert(exhibitionDO);
                return exhibitionRespVO;
        }

    ExhibitionDO convertExhibitionDO(ExhibitionSaveReqVO saveReqVO);
}