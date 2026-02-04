package com.syj.eplus.module.system.controller.admin.report.vo;

import com.syj.eplus.framework.common.entity.SimpleFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Schema(description = "管理后台 - 打印模板change Request VO")
@Data
public class ReportChangeSaveReqVO {

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

    @Schema(description = "模版业务类型 1：打印 2：导出", example = "1")
    private Integer businessType;

}