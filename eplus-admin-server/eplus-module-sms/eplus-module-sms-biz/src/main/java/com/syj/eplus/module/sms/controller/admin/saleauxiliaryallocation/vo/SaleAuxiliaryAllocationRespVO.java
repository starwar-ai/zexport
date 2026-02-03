package com.syj.eplus.module.sms.controller.admin.saleauxiliaryallocation.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
@Schema(description = "管理后台 - 销售合同辅料分摊 Response VO")
@Data
@ExcelIgnoreUnannotated
public class SaleAuxiliaryAllocationRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "7492")
    @ExcelProperty("主键")
    private Long id;
    
    @Schema(description = "销售合同主键", example = "2010")
    @ExcelProperty("销售合同主键")
    private Long saleContractId;
    
    @Schema(description = "销售合同编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("销售合同编号")
    private String saleContractCode;
    
    @Schema(description = "销售合同明细主键", example = "29166")
    @ExcelProperty("销售合同明细主键")
    private Long saleContractItemId;
    
    @Schema(description = "产品编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("产品编号")
    private String skuCode;
    
    @Schema(description = "产品名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @ExcelProperty("产品名称")
    private String skuName;
    
    @Schema(description = "客户货号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("客户货号")
    private String cskuCode;
    
    @Schema(description = "辅料采购合同主键", example = "30833")
    @ExcelProperty("辅料采购合同主键")
    private Long auxiliaryPurchaseContractId;
    
    @Schema(description = "辅料采购合同编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("辅料采购合同编号")
    private String auxiliaryPurchaseContractCode;
    
    @Schema(description = "辅料采购合同明细主键", example = "11334")
    @ExcelProperty("辅料采购合同明细主键")
    private Long auxiliaryPurchaseContractItemId;
    
    @Schema(description = "辅料产品编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("辅料产品编号")
    private String auxiliarySkuCode;
    
    @Schema(description = "辅料产品名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @ExcelProperty("辅料产品名称")
    private String auxiliarySkuName;
    
    @Schema(description = "采购数量", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("采购数量")
    private String quantity;
    
    @Schema(description = "分摊金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("分摊金额")
    private JsonAmount allocationAmount;
    
    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
    
}