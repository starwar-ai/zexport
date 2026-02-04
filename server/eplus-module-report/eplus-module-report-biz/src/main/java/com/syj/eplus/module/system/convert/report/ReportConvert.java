package com.syj.eplus.module.system.convert.report;

import com.syj.eplus.module.system.api.report.dto.ReportDTO;
import com.syj.eplus.module.system.controller.admin.report.vo.ReportInfoRespVO;
import com.syj.eplus.module.system.controller.admin.report.vo.ReportSaveReqVO;
import com.syj.eplus.module.system.controller.admin.report.vo.change.ChangeReportRespVO;
import com.syj.eplus.module.system.dal.dataobject.report.ReportChangeDO;
import com.syj.eplus.module.system.dal.dataobject.report.ReportDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper
public interface ReportConvert {

        ReportConvert INSTANCE = Mappers.getMapper(ReportConvert.class);

        ReportInfoRespVO convert(ReportDO reportDO);

        ReportDTO convertDTO(ReportDO reportDO);

        default ReportInfoRespVO convertReportInfoRespVO(ReportDO reportDO){
                ReportInfoRespVO reportInfoRespVO = convert(reportDO);
                return reportInfoRespVO;
        }

    default ReportDTO convertReportDTO(ReportDO reportDO){
                ReportDTO reportDTO = convertDTO(reportDO);
                return reportDTO;
        }

    ReportDO convertReportDO(ReportSaveReqVO saveReqVO);

    ChangeReportRespVO convertChangeReportRespVO(ReportChangeDO reportChangeDO);

        List<ChangeReportRespVO> convertChangeReportRespVOList(List<ReportChangeDO> list);

    ReportChangeDO convertReportChange(ReportInfoRespVO change);
}