package com.syj.eplus.module.dpms.convert.report;

import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import com.syj.eplus.module.dpms.controller.admin.report.vo.ReportRespVO;
import com.syj.eplus.module.dpms.controller.admin.report.vo.ReportSaveReqVO;
import com.syj.eplus.module.dpms.dal.dataobject.report.ReportDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper
public interface ReportConvert {

        ReportConvert INSTANCE = Mappers.getMapper(ReportConvert.class);

        ReportRespVO convert(ReportDO reportDO);

        default ReportRespVO convertReportRespVO(ReportDO reportDO){
                ReportRespVO reportRespVO = convert(reportDO);
                return reportRespVO;
        }

    ReportDO convertReportDO(ReportSaveReqVO saveReqVO);

        default List<ReportRespVO> convertReportRespVOList(List<ReportDO> reportDOList){
                return CollectionUtils.convertList(reportDOList,s->{
                        return convertReportRespVO(s);
                });
        }
}