package com.syj.eplus.module.oa.convert.feeshare;

import com.syj.eplus.module.oa.api.feeshare.dto.FeeShareReqDTO;
import com.syj.eplus.module.oa.controller.admin.feeshare.vo.FeeShareRespVO;
import com.syj.eplus.module.oa.controller.admin.feeshare.vo.FeeShareSaveReqVO;
import com.syj.eplus.module.oa.dal.dataobject.feeshare.FeeShareDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper
public interface FeeShareConvert {

        FeeShareConvert INSTANCE = Mappers.getMapper(FeeShareConvert.class);

        FeeShareRespVO convert(FeeShareDO feeShareDO);

        default FeeShareRespVO convertFeeShareRespVO(FeeShareDO feeShareDO){
                FeeShareRespVO feeShareRespVO = convert(feeShareDO);
                return feeShareRespVO;
        }

    FeeShareDO convertFeeShareDO(FeeShareSaveReqVO saveReqVO);
        List<FeeShareDO> convertFeeShareDOList(List<FeeShareSaveReqVO> saveReqVO);
        FeeShareDO convertFeeShareDOByDTO(FeeShareReqDTO feeShareDTO);
}