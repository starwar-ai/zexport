package com.syj.eplus.module.system.controller.admin.report.vo.change;

import com.syj.eplus.module.system.controller.admin.report.vo.ReportInfoRespVO;
import lombok.Data;

@Data
public class ChangeReportSaveReq {
    private ReportInfoRespVO old_shipment;
    private ReportInfoRespVO shipment;

    private Integer submitFlag;
}

