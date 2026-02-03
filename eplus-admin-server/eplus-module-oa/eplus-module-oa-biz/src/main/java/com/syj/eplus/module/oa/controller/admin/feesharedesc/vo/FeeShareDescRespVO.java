package com.syj.eplus.module.oa.controller.admin.feesharedesc.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
@Schema(description = "管理后台 - 费用归集具体名称 Response VO")
@Data
@ExcelIgnoreUnannotated
public class FeeShareDescRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "15141")
    @ExcelProperty("主键")
    private Long id;
    
    @Schema(description = "排序")
    @ExcelProperty("排序")
    private Integer sortNum;
    
    @Schema(description = "费用归集类型", example = "1")
    @ExcelProperty("费用归集类型")
    private Integer feeShareType;
    
    @Schema(description = "相关方类别", example = "2")
    @ExcelProperty("相关方类别")
    private Integer relationType;
    
    @Schema(description = "名称", example = "张三")
    @ExcelProperty("名称")
    private String name;
    
    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
    
}