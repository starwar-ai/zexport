package com.syj.eplus.module.dms.convert.forwarderfee;

import com.syj.eplus.module.dms.controller.admin.forwarderfee.vo.ForwarderFeeRespVO;
import com.syj.eplus.module.dms.controller.admin.forwarderfee.vo.ForwarderFeeSaveReqVO;
import com.syj.eplus.module.dms.dal.dataobject.forwarderfee.ForwarderFeeDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface ForwarderFeeConvert {

        ForwarderFeeConvert INSTANCE = Mappers.getMapper(ForwarderFeeConvert.class);

        ForwarderFeeRespVO convert(ForwarderFeeDO forwarderFeeDO);

        default ForwarderFeeRespVO convertForwarderFeeRespVO(ForwarderFeeDO forwarderFeeDO){
                ForwarderFeeRespVO forwarderFeeRespVO = convert(forwarderFeeDO);
                return forwarderFeeRespVO;
        }

    ForwarderFeeDO convertForwarderFeeDO(ForwarderFeeSaveReqVO saveReqVO);
}