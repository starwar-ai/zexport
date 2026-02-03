package com.syj.eplus.module.dpms.dal.mysql.report;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.dpms.controller.admin.report.vo.ReportPageReqVO;
import com.syj.eplus.module.dpms.dal.dataobject.report.ReportDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 报表配置 Mapper
 *
 * @author du
 */
@Mapper
public interface DynamicReportMapper extends BaseMapperX<ReportDO> {

    default PageResult<ReportDO> selectPage(ReportPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ReportDO>()
                .eqIfPresent(ReportDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ReportDO::getRoleId, reqVO.getRoleId())
                .likeIfPresent(ReportDO::getName, reqVO.getName())
                .eqIfPresent(ReportDO::getType, reqVO.getType())
                .eqIfPresent(ReportDO::getSort, reqVO.getSort())
                .betweenIfPresent(ReportDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ReportDO::getId));
    }

}