package com.syj.eplus.module.oa.convert.travel;


import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.oa.controller.admin.travelreimb.vo.TravelReimbRespVO;
import com.syj.eplus.module.oa.controller.admin.travelreimb.vo.TravelReimbSaveReqVO;
import com.syj.eplus.module.oa.controller.admin.reimb.vo.ReimbRespVO;
import com.syj.eplus.module.oa.dal.dataobject.reimb.ReimbDO;
import com.syj.eplus.module.oa.entity.JsonReimbDetail;
import com.syj.eplus.module.oa.enums.traveldetail.TravelExpenseTypeEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;

@Mapper
public interface TravelReimbConvert {

    TravelReimbConvert INSTANCE = Mappers.getMapper(TravelReimbConvert.class);

    ReimbDO convertReimbDO(TravelReimbSaveReqVO createReqVO);

    TravelReimbRespVO convertTravelReimbRespByReimbResp(ReimbRespVO reimbResp);
    default TravelReimbRespVO convertTravelReimbResp(ReimbRespVO reimbResp) {
        TravelReimbRespVO travelReimbRespVO = convertTravelReimbRespByReimbResp(reimbResp);
        List<JsonReimbDetail> reimbDetailList = reimbResp.getReimbDetailList();
        if (CollUtil.isNotEmpty(reimbDetailList)) {
            //将费用小项按照不同类型分组并返回给前台
            List<JsonReimbDetail> travelAllowanceList = reimbDetailList.stream()
                    .filter(detail -> detail.getExpenseType() == TravelExpenseTypeEnum.TRAVEL_ALLOWANCE.getCode())
                    .toList();

            List<JsonReimbDetail> accommodationFeeList = reimbDetailList.stream()
                    .filter(detail -> detail.getExpenseType() == TravelExpenseTypeEnum.ACCOMMODATION_FEE.getCode())
                    .toList();

            List<JsonReimbDetail> selfDrivingList = reimbDetailList.stream()
                    .filter(detail -> detail.getExpenseType() == TravelExpenseTypeEnum.SELF_DRIVING.getCode())
                    .toList();

            List<JsonReimbDetail> trafficFeeList = reimbDetailList.stream()
                    .filter(detail -> detail.getExpenseType() == TravelExpenseTypeEnum.TRAFFIC_FEE.getCode())
                    .toList();
            List<JsonReimbDetail> otherDescList = reimbDetailList.stream()
                    .filter(detail -> detail.getExpenseType() == TravelExpenseTypeEnum.OTHER_AMOUNT.getCode())
                    .toList();

            travelReimbRespVO.setTravelAllowanceList(travelAllowanceList);
            travelReimbRespVO.setSelfDrivingList(selfDrivingList);
            travelReimbRespVO.setAccommodationFeeList(accommodationFeeList);
            travelReimbRespVO.setTrafficFeeList(trafficFeeList);
            travelReimbRespVO.setOtherDescList(otherDescList);
        }
        return travelReimbRespVO;
    }

    default List<TravelReimbRespVO> convertTravelReimbListByReimbRespVO(List<ReimbRespVO> reimbResp){
        if (CollUtil.isEmpty(reimbResp)){
            return Collections.emptyList();
        }
        return reimbResp.stream().map(this::convertTravelReimbResp).toList();
    }

    default PageResult<TravelReimbRespVO> convertTravelPageResult(PageResult<ReimbRespVO> travelReimbPage) {
        if (CollUtil.isEmpty(travelReimbPage.getList())) {
            return PageResult.empty();
        }
        return new PageResult<TravelReimbRespVO>().setTotal(travelReimbPage.getTotal()).setList(convertTravelReimbListByReimbRespVO(travelReimbPage.getList()));
    }

    @Mapping(target = "invoiceList", ignore = true)
    @Mapping(target = "reimbType", ignore = true)
    @Mapping(target = "contractList", ignore = true)
    @Mapping(target = "submitFlag", ignore = true)
    TravelReimbSaveReqVO convertTravelSaveByResp(TravelReimbRespVO travelReimbRespVO);
}
