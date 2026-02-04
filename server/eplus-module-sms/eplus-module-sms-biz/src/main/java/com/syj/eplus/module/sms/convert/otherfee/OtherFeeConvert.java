package com.syj.eplus.module.sms.convert.otherfee;

import com.syj.eplus.module.sms.controller.admin.otherfee.vo.OtherFeeRespVO;
import com.syj.eplus.module.sms.controller.admin.otherfee.vo.OtherFeeSaveReqVO;
import com.syj.eplus.module.sms.dal.dataobject.otherfee.OtherFeeDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface OtherFeeConvert {

        OtherFeeConvert INSTANCE = Mappers.getMapper(OtherFeeConvert.class);

        OtherFeeRespVO convert(OtherFeeDO otherFeeDO);

        default OtherFeeRespVO convertOtherFeeRespVO(OtherFeeDO otherFeeDO){
                OtherFeeRespVO otherFeeRespVO = convert(otherFeeDO);
                return otherFeeRespVO;
        }

    OtherFeeDO convertOtherFeeDO(OtherFeeSaveReqVO saveReqVO);
}