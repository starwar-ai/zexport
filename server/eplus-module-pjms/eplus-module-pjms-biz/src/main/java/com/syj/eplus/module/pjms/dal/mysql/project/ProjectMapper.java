package com.syj.eplus.module.pjms.dal.mysql.project;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.pjms.controller.admin.project.vo.ProjectPageReqVO;
import com.syj.eplus.module.pjms.dal.dataobject.project.ProjectDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 项目 Mapper
 *
 * @author zhangcm
 */
@Mapper
public interface ProjectMapper extends BaseMapperX<ProjectDO> {

    default PageResult<ProjectDO> selectPage(ProjectPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ProjectDO>()
                .eqIfPresent(ProjectDO::getCode, reqVO.getCode())
                .likeIfPresent(ProjectDO::getName, reqVO.getName())
                .eqIfPresent(ProjectDO::getProjectStatus, reqVO.getProjectStatus())
                .eqIfPresent(ProjectDO::getAuditStatus, reqVO.getAuditStatus())
                .eqIfPresent(ProjectDO::getCompanyId, reqVO.getCompanyId())
                .likeIfPresent(ProjectDO::getCompanyName, reqVO.getCompanyName())
                .eqIfPresent(ProjectDO::getDevelopType, reqVO.getDevelopType())
                .betweenIfPresent(ProjectDO::getPlanStartDate, reqVO.getPlanStartDate())
                .betweenIfPresent(ProjectDO::getPlanEndDate, reqVO.getPlanEndDate())
                .betweenIfPresent(ProjectDO::getStartDate, reqVO.getStartDate())
                .betweenIfPresent(ProjectDO::getEndDate, reqVO.getEndDate())
                .eqIfPresent(ProjectDO::getOwnerUserId, reqVO.getOwnerUserId())
                .likeIfPresent(ProjectDO::getOwnerUserName, reqVO.getOwnerUserName())
                .eqIfPresent(ProjectDO::getOwnerDeptId, reqVO.getOwnerDeptId())
                .likeIfPresent(ProjectDO::getOwnerDeptName, reqVO.getOwnerDeptName())
                .eqIfPresent(ProjectDO::getBudget, reqVO.getBudget())
                .betweenIfPresent(ProjectDO::getApplyDate, reqVO.getApplyDate())
                .eqIfPresent(ProjectDO::getApplyUserId, reqVO.getApplyUserId())
                .likeIfPresent(ProjectDO::getApplyUserName, reqVO.getApplyUserName())
                .eqIfPresent(ProjectDO::getApplyDeptId, reqVO.getApplyDeptId())
                .likeIfPresent(ProjectDO::getApplyDeptName, reqVO.getApplyDeptName())
                .eqIfPresent(ProjectDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(ProjectDO::getDoneTime, reqVO.getDoneTime())
                .betweenIfPresent(ProjectDO::getFinishTime, reqVO.getFinishTime())
                .betweenIfPresent(ProjectDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ProjectDO::getId));
    }

}