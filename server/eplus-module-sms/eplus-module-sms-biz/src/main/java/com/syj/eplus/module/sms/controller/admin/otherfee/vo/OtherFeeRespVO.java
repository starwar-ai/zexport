package com.syj.eplus.module.sms.controller.admin.otherfee.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
@Schema(description = "管理后台 - 其他费用 Response VO")
@Data
@ExcelIgnoreUnannotated
public class OtherFeeRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "19294")
    @ExcelProperty("主键")
    private Long id;
    
    @Schema(description = "报价单id", example = "2927")
    @ExcelProperty("报价单id")
    private Long smsQuotationId;
    
    @Schema(description = "费用名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @ExcelProperty("费用名称")
    private String feeName;
    
    @Schema(description = "金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("金额")
    private JsonAmount amount;
    
    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
    
}