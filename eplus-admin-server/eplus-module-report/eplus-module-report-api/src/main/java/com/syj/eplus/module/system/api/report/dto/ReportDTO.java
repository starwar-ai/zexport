package com.syj.eplus.module.system.api.report.dto;

import com.syj.eplus.framework.common.entity.SimpleFile;
import lombok.Data;

import java.util.List;

/**
 * @Description：
 * @Author：chengbo
 * @Date：2024/2/22 15:39
 */
@Data
public class ReportDTO {
    private String code;

    private String name;

    private List<SimpleFile> annex;

    private Integer reportType;

    private Integer sourceType;

    private String sourceCode;

    private String sourceName;

    private String companyName;

    private Long companyId;

    private Integer auditStatus;
}