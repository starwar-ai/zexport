package com.syj.eplus.module.oa.controller.admin.reimbshare.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.module.crm.api.cust.dto.SimpleCustRespDTO;
import com.syj.eplus.module.scm.api.vender.dto.SimpleVenderRespDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Schema(description = "管理后台 - 报销分摊 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ReimbShareRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "10146")
    @ExcelProperty("主键")
    private Long reimbShareId;
    
    @Schema(description = "报销单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "28790")
    @ExcelProperty("报销单编号")
    private Long reimbId;
    
    @Schema(description = "费用归属类型", example = "1")
    @ExcelProperty("费用归属类型")
    private Integer auxiliaryType;
    
    @Schema(description = "费用归属对象id", requiredMode = Schema.RequiredMode.REQUIRED, example = "18626")
    private Long auxiliaryId;

    @Schema(description = "费用归属对象名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "18626")
    @ExcelProperty("费用归属对象名称")
    private Long auxiliaryName;
    
    @Schema(description = "分摊比例", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("分摊比例")
    private BigDecimal ratio;
    
    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    private SimpleCustRespDTO custRespDTO;

    private SimpleVenderRespDTO venderRespDTO;
    
}