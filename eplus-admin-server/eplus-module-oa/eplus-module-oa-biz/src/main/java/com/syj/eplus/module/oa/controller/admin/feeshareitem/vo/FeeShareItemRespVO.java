package com.syj.eplus.module.oa.controller.admin.feeshareitem.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
@Schema(description = "管理后台 - 费用归集明细 Response VO")
@Data
@ExcelIgnoreUnannotated
public class FeeShareItemRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "31512")
    @ExcelProperty("主键")
    private Long id;
    
    @Schema(description = "费用归属id", example = "29806")
    @ExcelProperty("费用归属id")
    private Long shareFeeId;
    
    @Schema(description = "费用归属对象类型", example = "1")
    @ExcelProperty("费用归属对象类型")
    private Integer businessSubjectType;
    
    @Schema(description = "费用归属对象编号")
    @ExcelProperty("费用归属对象编号")
    private Long businessSubjectCode;
    
    @Schema(description = "分摊比例")
    @ExcelProperty("分摊比例")
    private Integer ratio;
    
    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
    
}