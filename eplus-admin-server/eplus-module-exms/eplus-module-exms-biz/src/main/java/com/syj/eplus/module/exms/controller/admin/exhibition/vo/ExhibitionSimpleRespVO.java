package com.syj.eplus.module.exms.controller.admin.exhibition.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 展会 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ExhibitionSimpleRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "31339")
    @ExcelProperty("主键")
    private Long id;
    
    @Schema(description = "编号")
    @ExcelProperty("编号")
    private String code;
    
    @Schema(description = "名称", example = "芋艿")
    @ExcelProperty("名称")
    private String name;
    

    
}