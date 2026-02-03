package com.syj.eplus.module.dtms.dal.mysql.design;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.dtms.controller.admin.design.vo.DesignPageReqVO;
import com.syj.eplus.module.dtms.dal.dataobject.design.DesignDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 设计-任务单 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface DesignMapper extends BaseMapperX<DesignDO> {

    default PageResult<DesignDO> selectPage(DesignPageReqVO reqVO) {
        LambdaQueryWrapperX<DesignDO> lambdaQueryWrapperX = new LambdaQueryWrapperX<DesignDO>()
                .eqIfPresent(DesignDO::getCode, reqVO.getCode())
                .likeIfPresent(DesignDO::getName, reqVO.getName())
                .eqIfPresent(DesignDO::getDesignStatus, reqVO.getDesignStatus())
                .eqIfPresent(DesignDO::getSpecialPermissionFlag, reqVO.getSpecialPermissionFlag())
                .eqIfPresent(DesignDO::getContractCode, reqVO.getContractCode())
                .betweenIfPresent(DesignDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(DesignDO::getId);
        return selectPage(reqVO, lambdaQueryWrapperX);
    }

}
