package com.syj.eplus.module.oa.convert.reimb;


import com.syj.eplus.module.oa.api.dto.ReimbDetailDTO;
import com.syj.eplus.module.oa.controller.admin.travelreimb.vo.TravelReimbSaveReqVO;
import com.syj.eplus.module.oa.controller.admin.generalreimb.vo.GeneralReimbSaveReqVO;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbSaveReqVO;
import com.syj.eplus.module.oa.entity.JsonReimbDetail;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ReimbConvert {

    ReimbConvert INSTANCE = Mappers.getMapper(ReimbConvert.class);

    ReimbSaveReqVO convertReimbSaveReqVO(TravelReimbSaveReqVO updateReqVO);

    ReimbSaveReqVO convertReimbSaveReqVO(GeneralReimbSaveReqVO updateReqVO);

    JsonReimbDetail convertJsonReimbDetail(ReimbDetailDTO reimbDetailDTO);

    List<ReimbDetailDTO> convertReimbDetailDTOList(List<JsonReimbDetail> jsonReimbDetailList);
}
