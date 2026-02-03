package com.syj.eplus.module.oa.dal.mysql.reimbshare;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.oa.controller.admin.reimbshare.vo.ReimbSharePageReqVO;
import com.syj.eplus.module.oa.dal.dataobject.reimbshare.ReimbShareDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 报销分摊 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface ReimbShareMapper extends BaseMapperX<ReimbShareDO> {

    default PageResult<ReimbShareDO> selectPage(ReimbSharePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ReimbShareDO>()
                .eqIfPresent(ReimbShareDO::getReimbId, reqVO.getReimbId())
                .eqIfPresent(ReimbShareDO::getAuxiliaryType, reqVO.getAuxiliaryType())
                .eqIfPresent(ReimbShareDO::getAuxiliaryId, reqVO.getAuxiliaryId())
                .eqIfPresent(ReimbShareDO::getRatio, reqVO.getRatio())
                .betweenIfPresent(ReimbShareDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ReimbShareDO::getReimbShareId));
    }

}