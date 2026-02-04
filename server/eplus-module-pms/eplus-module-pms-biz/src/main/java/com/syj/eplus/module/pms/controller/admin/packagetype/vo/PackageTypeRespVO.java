package com.syj.eplus.module.pms.controller.admin.packagetype.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
@Schema(description = "管理后台 - 包装方式 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PackageTypeRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "23942")
    @ExcelProperty("主键")
    private Long id;
    
    @Schema(description = "包装方式编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("包装方式编号")
    private String code;
    
    @Schema(description = "包装方式名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @ExcelProperty("包装方式名称")
    private String name;
    
    @Schema(description = "包装方式英文名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("包装方式英文名称")
    private String nameEng;
    
    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
    
}