package com.syj.eplus.module.qms.convert.qualityinspection;

import com.syj.eplus.module.qms.controller.admin.qualityinspection.vo.QualityInspectionRespVO;
import com.syj.eplus.module.qms.controller.admin.qualityinspection.vo.QualityInspectionSaveReqVO;
import com.syj.eplus.module.qms.dal.dataobject.qualityinspection.QualityInspectionDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface QualityInspectionConvert {

        QualityInspectionConvert INSTANCE = Mappers.getMapper(QualityInspectionConvert.class);

        QualityInspectionRespVO convert(QualityInspectionDO qualityInspectionDO);

        default QualityInspectionRespVO convertQualityInspectionRespVO(QualityInspectionDO qualityInspectionDO){
                QualityInspectionRespVO qualityInspectionRespVO = convert(qualityInspectionDO);
                return qualityInspectionRespVO;
        }

    QualityInspectionDO convertQualityInspectionDO(QualityInspectionSaveReqVO saveReqVO);
}
