package com.syj.eplus.module.system.api.report;

import com.syj.eplus.module.system.api.report.dto.ReportDTO;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * @Description：模板相关api (外部调用)
 * @Author：chengbo
 * @Date：2024/6/19 15:39
 */
public interface ReportApi {
    /**
     * 通过编号获取模板对应名称(code为空默认查全部)
     */
    ReportDTO getReport(String code);

    ReportDTO getCompanyReport(String code,Long companyId);
    /**
     * 获取外部模板
     */
    ReportDTO getSourceReport(String code, String sourceCode, Integer sourceType, Long companyId);
    /**
     * 获取可选模板
     */
    ReportDTO getReportById(Long reportId);
    /**
     * 打印
     */
    String print(String inputPath, String outputPath, HashMap<String, Object> params, String outputName);

    /**
     * pdf导出
     */
    void exportWord(HttpServletResponse response, String inputPath, String outputPath, HashMap<String, Object> params);
}
