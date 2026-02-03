package com.syj.eplus.module.dms.convert.forwardercompanyinfo;

import com.syj.eplus.module.dms.controller.admin.forwardercompanyinfo.vo.ForwarderCompanyInfoRespVO;
import com.syj.eplus.module.dms.controller.admin.forwardercompanyinfo.vo.ForwarderCompanyInfoSaveReqVO;
import com.syj.eplus.module.dms.dal.dataobject.forwardercompanyinfo.ForwarderCompanyInfoDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface ForwarderCompanyInfoConvert {

        ForwarderCompanyInfoConvert INSTANCE = Mappers.getMapper(ForwarderCompanyInfoConvert.class);

        ForwarderCompanyInfoRespVO convert(ForwarderCompanyInfoDO forwarderCompanyInfoDO);

        default ForwarderCompanyInfoRespVO convertForwarderCompanyInfoRespVO(ForwarderCompanyInfoDO forwarderCompanyInfoDO){
                ForwarderCompanyInfoRespVO forwarderCompanyInfoRespVO = convert(forwarderCompanyInfoDO);
                return forwarderCompanyInfoRespVO;
        }

    ForwarderCompanyInfoDO convertForwarderCompanyInfoDO(ForwarderCompanyInfoSaveReqVO saveReqVO);
}