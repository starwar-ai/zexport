package com.syj.eplus.module.system.controller.admin.report.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import com.syj.eplus.framework.common.entity.SimpleFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 打印模板分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ReportPageReqVO extends PageParam {

    @Schema(description = "模板编码")
    private String code;

    @Schema(description = "模板名称", example = "芋艿")
    private String name;

    @Schema(description = "模板")
    private List<SimpleFile> annex;

    @Schema(description = "模板类型：1：基础模板，2：外部模板，3：可选模板", example = "1")
    private Integer reportType;

    @Schema(description = "外部模板类型：1：客户，2：供应商", example = "1")
    private Integer sourceType;

    @Schema(description = "来源编号")
    private String sourceCode;

    @Schema(description = "来源名称", example = "张三")
    private String sourceName;

    @Schema(description = "审核状态", example = "1")
    private Integer auditStatus;

    @Schema(description = "账套id")
    private Long companyId;

    @Schema(description = "账套名称")
    private String companyName;

    @Schema(description = "模版业务类型", example = "1")
    private Integer businessType;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}