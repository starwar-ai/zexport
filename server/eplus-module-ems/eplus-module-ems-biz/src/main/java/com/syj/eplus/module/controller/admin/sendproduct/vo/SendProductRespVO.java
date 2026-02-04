package com.syj.eplus.module.controller.admin.sendproduct.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.SimpleFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
@Schema(description = "管理后台 - 寄件产品 Response VO")
@Data
@ExcelIgnoreUnannotated
public class SendProductRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "22556")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "寄件id")
    @ExcelProperty("寄件id")
    private Long sendId;
    
    @Schema(description = "序号")
    @ExcelProperty("序号")
    private Integer sortNum;
    
    @Schema(description = "物件来源")
    @ExcelProperty("物件来源")
    private Integer goodsSource;
    
    @Schema(description = "产品编号")
    @ExcelProperty("产品编号")
    private String skuCode;
    
    @Schema(description = "产品名称", example = "张三")
    @ExcelProperty("产品名称")
    private String skuName;
    
    @Schema(description = "图片", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("图片")
    private SimpleFile picture;
    
    @Schema(description = "数量")
    @ExcelProperty("数量")
    private Integer quantity;
    
    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    //前端传参
    @Schema(description = "图片", requiredMode = Schema.RequiredMode.REQUIRED)
    private SimpleFile mainPicture;
}