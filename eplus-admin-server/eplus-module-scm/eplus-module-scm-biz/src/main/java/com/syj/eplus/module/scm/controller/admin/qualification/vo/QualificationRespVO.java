package com.syj.eplus.module.scm.controller.admin.qualification.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
@Schema(description = "管理后台 - 资质 Response VO")
@Data
@ExcelIgnoreUnannotated
public class QualificationRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "21084")
    @ExcelProperty("主键")
    private Long id;
    
    @Schema(description = "表单名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @ExcelProperty("表单名称")
    private String name;
    
    @Schema(description = "资质描述", requiredMode = Schema.RequiredMode.REQUIRED, example = "你猜")
    @ExcelProperty("资质描述")
    private String description;
    
    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
    
}