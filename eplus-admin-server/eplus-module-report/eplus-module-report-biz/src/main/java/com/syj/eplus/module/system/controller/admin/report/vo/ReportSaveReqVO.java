package com.syj.eplus.module.system.controller.admin.report.vo;

import com.syj.eplus.framework.common.entity.SimpleFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "管理后台 - 打印模板新增/修改 Request VO")
@Data
public class ReportSaveReqVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "32219")
    private Long id;

    @Schema(description = "模板编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "模板编码不能为空")
    private String code;

    @Schema(description = "模板名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @NotEmpty(message = "模板名称不能为空")
    private String name;

    @Schema(description = "模板")
    private List<SimpleFile> annex;

    @Schema(description = "模板类型：1：基础模板，2：外部模板，3：可选模板", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "模板类型不能为空")
    private Integer reportType;

    @Schema(description = "外部模板类型：1：客户，2：供应商", example = "1")
    private Integer sourceType;

    @Schema(description = "来源编号")
    private String sourceCode;

    @Schema(description = "来源名称", example = "张三")
    private String sourceName;

    @Schema(description = "审核状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer auditStatus;

    @Schema(description = "账套id")
    private Long companyId;

    @Schema(description = "账套名称")
    private String companyName;

    private Integer submitFlag;

    @Schema(description = "模版业务类型 1：打印 2：导出", example = "1")
    private Integer businessType;

}