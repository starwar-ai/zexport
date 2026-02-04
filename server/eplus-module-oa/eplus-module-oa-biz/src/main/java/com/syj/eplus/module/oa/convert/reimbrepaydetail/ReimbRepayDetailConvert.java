package com.syj.eplus.module.oa.convert.reimbrepaydetail;

import com.syj.eplus.module.oa.controller.admin.reimbrepaydetail.vo.ReimbRepayDetailRespVO;
import com.syj.eplus.module.oa.controller.admin.reimbrepaydetail.vo.ReimbRepayDetailSaveReqVO;
import com.syj.eplus.module.oa.dal.dataobject.reimbrepaydetail.ReimbRepayDetailDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper
public interface ReimbRepayDetailConvert {

    ReimbRepayDetailConvert INSTANCE = Mappers.getMapper(ReimbRepayDetailConvert.class);

    ReimbRepayDetailRespVO convert(ReimbRepayDetailDO reimbRepayDetailDO);

    default ReimbRepayDetailRespVO convertReimbRepayDetailRespVO(ReimbRepayDetailDO reimbRepayDetailDO) {
        ReimbRepayDetailRespVO reimbRepayDetailRespVO = convert(reimbRepayDetailDO);
        return reimbRepayDetailRespVO;
    }

    ReimbRepayDetailDO convertReimbRepayDetailDO(ReimbRepayDetailSaveReqVO saveReqVO);

    List<ReimbRepayDetailDO> convertReimbRepayDetailDOList(List<ReimbRepayDetailSaveReqVO> saveReqVO);
}