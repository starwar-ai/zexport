package com.syj.eplus.module.pms.controller.admin.skuauxiliary.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
@Schema(description = "管理后台 - 产品辅料配比 Response VO")
@Data
@ExcelIgnoreUnannotated
public class SkuAuxiliaryRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "8070")
    @ExcelProperty("主键")
    private Long id;
    
    @Schema(description = "产品编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("产品编号")
    private String skuCode;
    
    @Schema(description = "产品名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @ExcelProperty("产品名称")
    private String skuName;
    
    @Schema(description = "辅料编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("辅料编号")
    private String auxiliarySkuCode;
    
    @Schema(description = "辅料名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @ExcelProperty("辅料名称")
    private String auxiliarySkuName;
    
    @Schema(description = "产品比", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("产品比")
    private Integer skuRate;
    
    @Schema(description = "辅料比", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("辅料比")
    private Integer auxiliarySkuRate;
    
    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
    
}