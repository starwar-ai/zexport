package com.syj.eplus.module.infra.controller.admin.companypath.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.JsonCompanyPath;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 抛砖路径 Response VO")
@Data
@ExcelIgnoreUnannotated
public class CompanyPathRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "5183")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "路径", requiredMode = Schema.RequiredMode.REQUIRED)
    private JsonCompanyPath path;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;

    @Schema(description = "描述", requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;
}