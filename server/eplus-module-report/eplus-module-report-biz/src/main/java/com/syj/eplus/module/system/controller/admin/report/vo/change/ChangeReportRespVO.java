package com.syj.eplus.module.system.controller.admin.report.vo.change;

import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.enums.DictTypeConstants;
import com.syj.eplus.module.system.controller.admin.report.vo.ReportInfoRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
public class ChangeReportRespVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "32219")
    @ExcelProperty("id")
    private Long id;

    @Schema(description = "模板编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("模板编码")
    private String code;

    @Schema(description = "模板名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @ExcelProperty("模板名称")
    private String name;

    @Schema(description = "模板")
    private List<SimpleFile> annex;

    @Schema(description = "模板类型：1：基础模板，2：外部模板，3：可选模板", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("模板类型")
    private Integer reportType;

    @Schema(description = "外部模板类型：1：客户，2：供应商", example = "1")
    @ExcelProperty("外部模板类型：1：客户，2：供应商")
    private Integer sourceType;

    @Schema(description = "来源编号")
    @ExcelProperty("来源编号")
    private String sourceCode;

    @Schema(description = "来源名称", example = "张三")
    @ExcelProperty("来源名称")
    private String sourceName;

    @Schema(description = "账套id")
    @ExcelProperty("账套id")
    private Long companyId;

    @Schema(description = "账套名称")
    @ExcelProperty("账套名称")
    private String companyName;

    @Schema(description = "模版业务类型")
    @ExcelProperty("模版业务类型")
    private Integer businessType;

    @Schema(description = "审核状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "审核状态", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.AUDIT_STATUS)
    private Integer auditStatus;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    private ReportInfoRespVO oldData;

    private String processInstanceId;

    private Integer submitFlag;
}
