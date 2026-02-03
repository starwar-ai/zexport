package com.syj.eplus.module.dpms.convert.reportrole;

import com.syj.eplus.module.dpms.controller.admin.reportrole.vo.ReportRoleRespVO;
import com.syj.eplus.module.dpms.controller.admin.reportrole.vo.ReportRoleSaveReqVO;
import com.syj.eplus.module.dpms.dal.dataobject.reportrole.ReportRoleDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface ReportRoleConvert {

        ReportRoleConvert INSTANCE = Mappers.getMapper(ReportRoleConvert.class);

        ReportRoleRespVO convert(ReportRoleDO reportRoleDO);

        default ReportRoleRespVO convertReportRoleRespVO(ReportRoleDO reportRoleDO){
                ReportRoleRespVO reportRoleRespVO = convert(reportRoleDO);
                return reportRoleRespVO;
        }

    ReportRoleDO convertReportRoleDO(ReportRoleSaveReqVO saveReqVO);
}