package com.syj.eplus.module.scm.convert.qualification;

import com.syj.eplus.module.scm.controller.admin.qualification.vo.QualificationRespVO;
import com.syj.eplus.module.scm.controller.admin.qualification.vo.QualificationSaveReqVO;
import com.syj.eplus.module.scm.dal.dataobject.qualification.QualificationDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface QualificationConvert {

        QualificationConvert INSTANCE = Mappers.getMapper(QualificationConvert.class);

        QualificationRespVO convert(QualificationDO qualificationDO);

        default QualificationRespVO convertQualificationRespVO(QualificationDO qualificationDO){
                QualificationRespVO qualificationRespVO = convert(qualificationDO);
                return qualificationRespVO;
        }

    QualificationDO convertQualificationDO(QualificationSaveReqVO saveReqVO);
}