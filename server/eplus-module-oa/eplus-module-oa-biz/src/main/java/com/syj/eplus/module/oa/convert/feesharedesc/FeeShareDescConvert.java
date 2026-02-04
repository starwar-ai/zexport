package com.syj.eplus.module.oa.convert.feesharedesc;

import com.syj.eplus.module.oa.controller.admin.feesharedesc.vo.FeeShareDescRespVO;
import com.syj.eplus.module.oa.controller.admin.feesharedesc.vo.FeeShareDescSaveReqVO;
import com.syj.eplus.module.oa.dal.dataobject.feesharedesc.FeeShareDescDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FeeShareDescConvert {

        FeeShareDescConvert INSTANCE = Mappers.getMapper(FeeShareDescConvert.class);

        FeeShareDescRespVO convert(FeeShareDescDO feeShareDescDO);

        default FeeShareDescRespVO convertFeeShareDescRespVO(FeeShareDescDO feeShareDescDO){
                FeeShareDescRespVO feeShareDescRespVO = convert(feeShareDescDO);
                return feeShareDescRespVO;
        }

    FeeShareDescDO convertFeeShareDescDO(FeeShareDescSaveReqVO saveReqVO);
}