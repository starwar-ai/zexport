package com.syj.eplus.module.dtms.dal.mysql.designsummary;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.dtms.controller.admin.designsummary.vo.DesignSummaryPageReqVO;
import com.syj.eplus.module.dtms.dal.dataobject.designsummary.DesignSummaryDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 设计-工作总结 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface DesignSummaryMapper extends BaseMapperX<DesignSummaryDO> {

    default PageResult<DesignSummaryDO> selectPage(DesignSummaryPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<DesignSummaryDO>()
                .eqIfPresent(DesignSummaryDO::getDesignId, reqVO.getDesignId())
                .eqIfPresent(DesignSummaryDO::getDesignItemId, reqVO.getDesignItemId())
                .eqIfPresent(DesignSummaryDO::getDesignerId, reqVO.getDesignerId())
                .likeIfPresent(DesignSummaryDO::getDesignerName, reqVO.getDesignerName())
                .eqIfPresent(DesignSummaryDO::getDesignerDeptId, reqVO.getDesignerDeptId())
                .likeIfPresent(DesignSummaryDO::getDesignerDeptName, reqVO.getDesignerDeptName())
                .eqIfPresent(DesignSummaryDO::getProgress, reqVO.getProgress())
                .eqIfPresent(DesignSummaryDO::getProgressDesc, reqVO.getProgressDesc())
                .eqIfPresent(DesignSummaryDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(DesignSummaryDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(DesignSummaryDO::getId));
    }

}
