package com.syj.eplus.module.oa.convert.dictsubject;

import com.syj.eplus.module.oa.controller.admin.dictsubject.vo.DictSubjectRespVO;
import com.syj.eplus.module.oa.controller.admin.dictsubject.vo.DictSubjectSaveReqVO;
import com.syj.eplus.module.oa.dal.dataobject.dictsubject.DictSubjectDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface DictSubjectConvert {

        DictSubjectConvert INSTANCE = Mappers.getMapper(DictSubjectConvert.class);

        DictSubjectRespVO convert(DictSubjectDO dictSubjectDO);

        default DictSubjectRespVO convertDictSubjectRespVO(DictSubjectDO dictSubjectDO){
                DictSubjectRespVO dictSubjectRespVO = convert(dictSubjectDO);
                return dictSubjectRespVO;
        }

    DictSubjectDO convertDictSubjectDO(DictSubjectSaveReqVO saveReqVO);
}