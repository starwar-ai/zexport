package com.syj.eplus.module.system.dal.mysql.report;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.system.controller.admin.report.vo.ReportPageReqVO;
import com.syj.eplus.module.system.dal.dataobject.report.ReportDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 打印模板 Mapper
 *
 * @author ePlus
 */
@Mapper
public interface ReportMapper extends BaseMapperX<ReportDO> {

    default PageResult<ReportDO> selectPage(ReportPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ReportDO>()
                .eqIfPresent(ReportDO::getCode, reqVO.getCode())
                .likeIfPresent(ReportDO::getName, reqVO.getName())
                .eqIfPresent(ReportDO::getReportType, reqVO.getReportType())
                .eqIfPresent(ReportDO::getSourceType, reqVO.getSourceType())
                .eqIfPresent(ReportDO::getSourceCode, reqVO.getSourceCode())
                .likeIfPresent(ReportDO::getSourceName, reqVO.getSourceName())
                .betweenIfPresent(ReportDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ReportDO::getId));
    }

}