package com.syj.eplus.module.oa.convert.reimbshare;

import com.syj.eplus.module.oa.controller.admin.reimbshare.vo.ReimbShareRespVO;
import com.syj.eplus.module.oa.controller.admin.reimbshare.vo.ReimbShareSaveReqVO;
import com.syj.eplus.module.oa.dal.dataobject.reimbshare.ReimbShareDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper
public interface ReimbShareConvert {

        ReimbShareConvert INSTANCE = Mappers.getMapper(ReimbShareConvert.class);

        ReimbShareRespVO convert(ReimbShareDO reimbShareDO);

        default ReimbShareRespVO convertReimbShareRespVO(ReimbShareDO reimbShareDO){
                ReimbShareRespVO reimbShareRespVO = convert(reimbShareDO);
                return reimbShareRespVO;
        }

    ReimbShareDO convertReimbShareDO(ReimbShareSaveReqVO saveReqVO);
        List<ReimbShareDO> convertReimbShareDOList(List<ReimbShareSaveReqVO> saveReqVO);
}