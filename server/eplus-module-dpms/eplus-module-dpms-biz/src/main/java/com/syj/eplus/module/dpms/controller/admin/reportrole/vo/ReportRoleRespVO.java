package com.syj.eplus.module.dpms.controller.admin.reportrole.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
@Schema(description = "管理后台 - 报表角色关系表 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ReportRoleRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "3773")
    @ExcelProperty("主键")
    private Long id;
    
    @Schema(description = "角色编号", example = "30354")
    @ExcelProperty("角色编号")
    private Long roleId;
    
    @Schema(description = "报表编号", example = "8413")
    @ExcelProperty("报表编号")
    private Long reportId;
    
    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
    
}