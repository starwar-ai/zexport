package com.syj.eplus.module.exms.controller.admin.eventcategory.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
@Schema(description = "管理后台 - 展会系列 Response VO")
@Data
@ExcelIgnoreUnannotated
public class EventCategoryRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "26915")
    @ExcelProperty("主键")
    private Long id;
    
    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @ExcelProperty("名称")
    private String name;
    
    @Schema(description = "是否国内系列 0：否 1：是")
    @ExcelProperty("是否国内系列 0：否 1：是")
    private Integer isDomestic;
    
    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
    
}