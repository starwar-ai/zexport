package com.syj.eplus.module.scm.convert.concessionrelease;

import com.syj.eplus.module.scm.controller.admin.concessionrelease.vo.ConcessionReleaseRespVO;
import com.syj.eplus.module.scm.controller.admin.concessionrelease.vo.ConcessionReleaseSaveReqVO;
import com.syj.eplus.module.scm.dal.dataobject.concessionrelease.ConcessionReleaseDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface ConcessionReleaseConvert {

        ConcessionReleaseConvert INSTANCE = Mappers.getMapper(ConcessionReleaseConvert.class);

        ConcessionReleaseRespVO convert(ConcessionReleaseDO concessionReleaseDO);

        default ConcessionReleaseRespVO convertConcessionReleaseRespVO(ConcessionReleaseDO concessionReleaseDO){
                ConcessionReleaseRespVO concessionReleaseRespVO = convert(concessionReleaseDO);
                return concessionReleaseRespVO;
        }

    ConcessionReleaseDO convertConcessionReleaseDO(ConcessionReleaseSaveReqVO saveReqVO);
}