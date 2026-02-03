package com.syj.eplus.module.mms.dal.mysql.manufacture;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.mms.controller.admin.manufacture.vo.ManufacturePageReqVO;
import com.syj.eplus.module.mms.dal.dataobject.manufacture.ManufactureDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 加工单 Mapper
 *
 * @author zhangcm
 */
@Mapper
public interface ManufactureMapper extends BaseMapperX<ManufactureDO> {

    default PageResult<ManufactureDO> selectPage(ManufacturePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ManufactureDO>()
                .eqIfPresent(ManufactureDO::getCode, reqVO.getCode())
                .eqIfPresent(ManufactureDO::getInputUserId, reqVO.getInputUserId())
                .likeIfPresent(ManufactureDO::getInputUserName, reqVO.getInputUserName())
                .betweenIfPresent(ManufactureDO::getInputTime, reqVO.getInputTime())
                .eqIfPresent(ManufactureDO::getStockId, reqVO.getStockId())
                .likeIfPresent(ManufactureDO::getStockName, reqVO.getStockName())
                .eqIfPresent(ManufactureDO::getCompanyId, reqVO.getCompanyId())
                .likeIfPresent(ManufactureDO::getCompanyName, reqVO.getCompanyName())
                .eqIfPresent(ManufactureDO::getCustId, reqVO.getCustId())
                .eqIfPresent(ManufactureDO::getCustCode, reqVO.getCustCode())
                .likeIfPresent(ManufactureDO::getCustName, reqVO.getCustName())
                .eqIfPresent(ManufactureDO::getManufactureStatus, reqVO.getManufactureStatus())
                .eqIfPresent(ManufactureDO::getAutoFlag, reqVO.getAutoFlag())
                .betweenIfPresent(ManufactureDO::getDoneTime, reqVO.getDoneTime())
                .betweenIfPresent(ManufactureDO::getFinishTime, reqVO.getFinishTime())
                .eqIfPresent(ManufactureDO::getFinishDesc, reqVO.getFinishDesc())
                .betweenIfPresent(ManufactureDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ManufactureDO::getId));
    }

}