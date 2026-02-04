package com.syj.eplus.module.dpms.dal.mysql.reportrole;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.syj.eplus.module.dpms.controller.admin.reportrole.vo.ReportRolePageReqVO;
import com.syj.eplus.module.dpms.dal.dataobject.reportrole.ReportRoleDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * 报表角色关系表 Mapper
 *
 * @author du
 */
@Mapper
public interface ReportRoleMapper extends BaseMapperX<ReportRoleDO> {

    default PageResult<ReportRoleDO> selectPage(ReportRolePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ReportRoleDO>()
                .eqIfPresent(ReportRoleDO::getRoleId, reqVO.getRoleId())
                .eqIfPresent(ReportRoleDO::getReportId, reqVO.getReportId())
                .betweenIfPresent(ReportRoleDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ReportRoleDO::getId));
    }

    default List<ReportRoleDO> selectListByRoleId(Long roleId) {
        return selectList(ReportRoleDO::getRoleId, roleId);
    }

    default void deleteListByRoleIdAndCardIds(Long roleId, Collection<Long> reportIds) {
        delete(new LambdaQueryWrapper<ReportRoleDO>()
                .eq(ReportRoleDO::getRoleId, roleId)
                .in(ReportRoleDO::getReportId, reportIds));
    }
}