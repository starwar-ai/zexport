package com.syj.eplus.module.system.api;

import com.syj.eplus.module.system.api.report.ReportApi;
import com.syj.eplus.module.system.api.report.dto.ReportDTO;
import com.syj.eplus.module.system.service.report.ReportService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * @Description：
 * @Author：du
 * @Date：2024/2/22 15:41
 */
@Service
public class ReportApiImpl implements ReportApi {
    @Resource
    private ReportService reportService;

    @Override
    public ReportDTO getReport(String code) {
        return reportService.getReportByCode(code);
    }
    @Override
    public ReportDTO getCompanyReport(String code,Long companyId) {
        return reportService.getReportByCodeAndCompanyId(code,companyId);
    }

    @Override
    public ReportDTO getSourceReport(String code, String sourceCode, Integer sourceType,Long companyId) {
        return reportService.getSourceReport(code, sourceCode, sourceType,companyId);
    }

    @Override
    public ReportDTO getReportById(Long reportId) {
        return reportService.getReportById(reportId);
    }

    @Override
    public String print(String inputPath, String outputPath, HashMap<String, Object> params, String outputName) {
        return reportService.print(inputPath, outputPath, params,outputName);
    }

    @Override
    public void exportWord(HttpServletResponse response, String inputPath, String outputPath, HashMap<String, Object> params) {
       reportService.exportWord(response,inputPath, outputPath, params);
    }

}
