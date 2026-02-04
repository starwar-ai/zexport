package com.syj.eplus.module.system.dal.mysql.reportchange;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.system.controller.admin.report.vo.ReportPageReqVO;
import com.syj.eplus.module.system.dal.dataobject.report.ReportChangeDO;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface ReportChangeMapper extends BaseMapperX<ReportChangeDO> {

    default PageResult<ReportChangeDO> selectPage(ReportPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ReportChangeDO>()
                .orderByDesc(ReportChangeDO::getId));
    }

}