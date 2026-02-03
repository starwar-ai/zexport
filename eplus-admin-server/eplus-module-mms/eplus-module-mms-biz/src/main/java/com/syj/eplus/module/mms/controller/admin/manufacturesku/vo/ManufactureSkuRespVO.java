package com.syj.eplus.module.mms.controller.admin.manufacturesku.vo;


import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.SimpleFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 加工单产品 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ManufactureSkuRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "12773")
    @ExcelProperty("主键")
    private Long id;
    
    @Schema(description = "加工单id", example = "9979")
    @ExcelProperty("加工单id")
    private Long manufactureId;
    
    @Schema(description = "产品id", example = "6010")
    @ExcelProperty("产品id")
    private Long skuId;
    
    @Schema(description = "产品编号")
    @ExcelProperty("产品编号")
    private String skuCode;
    
    @Schema(description = "客户产品编号")
    @ExcelProperty("客户产品编号")
    private String cskuCode;
    
    @Schema(description = "产品名称", example = "芋艿")
    @ExcelProperty("产品名称")
    private String skuName;
    
    @Schema(description = "产品数量")
    @ExcelProperty("产品数量")
    private Integer quantity;
    
    @Schema(description = "产品图片")
    @ExcelProperty("产品图片")
    private SimpleFile mainPicture;
    
    @Schema(description = "销售合同id", example = "7925")
    @ExcelProperty("销售合同id")
    private Long smsContractId;
    
    @Schema(description = "销售合同编号")
    @ExcelProperty("销售合同编号")
    private String smsContractCode;
    
    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
    
}