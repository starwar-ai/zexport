package com.syj.eplus.module.dms.dal.mysql.forwarderfee;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.dms.controller.admin.forwarderfee.vo.ForwarderFeePageReqVO;
import com.syj.eplus.module.dms.dal.dataobject.forwarderfee.ForwarderFeeDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 船代费用 Mapper
 *
 * @author zhangcm
 */
@Mapper
public interface ForwarderFeeMapper extends BaseMapperX<ForwarderFeeDO> {

    default PageResult<ForwarderFeeDO> selectPage(ForwarderFeePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ForwarderFeeDO>()
                .eqIfPresent(ForwarderFeeDO::getShipmentId, reqVO.getShipmentId())
                .eqIfPresent(ForwarderFeeDO::getShipmentCode, reqVO.getShipmentCode())
                .eqIfPresent(ForwarderFeeDO::getSortNum, reqVO.getSortNum())
                .eqIfPresent(ForwarderFeeDO::getVenderId, reqVO.getVenderId())
                .eqIfPresent(ForwarderFeeDO::getVenderCode, reqVO.getVenderCode())
                .likeIfPresent(ForwarderFeeDO::getVenderName, reqVO.getVenderName())
                .eqIfPresent(ForwarderFeeDO::getDictSubjectId, reqVO.getDictSubjectId())
                .likeIfPresent(ForwarderFeeDO::getDictSubjectName, reqVO.getDictSubjectName())
                .eqIfPresent(ForwarderFeeDO::getFeeType, reqVO.getFeeType())
                .eqIfPresent(ForwarderFeeDO::getAmount, reqVO.getAmount())
                .eqIfPresent(ForwarderFeeDO::getPayStatus, reqVO.getPayStatus())
                .eqIfPresent(ForwarderFeeDO::getApplyer, reqVO.getApplyer())
                .betweenIfPresent(ForwarderFeeDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ForwarderFeeDO::getId));
    }

}