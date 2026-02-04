package com.syj.eplus.module.mms.controller.admin.manufactureskuitem.vo;


import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.SimpleFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 加工单子产品 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ManufactureSkuItemRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "10158")
    @ExcelProperty("主键")
    private Long id;
    
    @Schema(description = "加工单产品id", example = "17983")
    @ExcelProperty("加工单产品id")
    private Long manufactureSkuId;
    
    @Schema(description = "加工单id", example = "2510")
    @ExcelProperty("加工单id")
    private Long manufactureId;
    
    @Schema(description = "产品id", example = "2482")
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
    
    @Schema(description = "配比")
    @ExcelProperty("配比")
    private Integer ratio;
    
    @Schema(description = "产品图片")
    @ExcelProperty("产品图片")
    private SimpleFile mainPicture;
    
    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
    
}