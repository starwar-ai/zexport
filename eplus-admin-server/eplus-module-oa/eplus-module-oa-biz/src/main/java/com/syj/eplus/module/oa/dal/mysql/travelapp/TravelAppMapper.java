package com.syj.eplus.module.oa.dal.mysql.travelapp;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.oa.controller.admin.travelapp.vo.SimpleTravelAppPageReqVO;
import com.syj.eplus.module.oa.controller.admin.travelapp.vo.TravelAppPageReqVO;
import com.syj.eplus.module.oa.dal.dataobject.travelapp.TravelAppDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 出差申请 Mapper
 *
 * @author 管理员
 */
@Mapper
public interface TravelAppMapper extends BaseMapperX<TravelAppDO> {

    default PageResult<TravelAppDO> selectPage(TravelAppPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<TravelAppDO>()
                .eqIfPresent(TravelAppDO::getPurpose, reqVO.getPurpose())
                .eqIfPresent(TravelAppDO::getDest, reqVO.getDest())
                .eqIfPresent(TravelAppDO::getAuditStatus, reqVO.getAuditStatus())
                .betweenIfPresent(TravelAppDO::getStartTime, reqVO.getStartTime())
                .betweenIfPresent(TravelAppDO::getEndTime, reqVO.getEndTime())
                .eqIfPresent(TravelAppDO::getDuration, reqVO.getDuration())
                .eqIfPresent(TravelAppDO::getTransportationType, reqVO.getTransportationType())
                .eqIfPresent(TravelAppDO::getCompanions, reqVO.getCompanions())
                .betweenIfPresent(TravelAppDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(TravelAppDO::getId));
    }

    default PageResult<TravelAppDO> selectSimplePage(SimpleTravelAppPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<TravelAppDO>()
                .eqIfPresent(TravelAppDO::getApplyerId, reqVO.getApplyerId())
                .betweenIfPresent(TravelAppDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(TravelAppDO::getId));
    }

}