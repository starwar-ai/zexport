package com.syj.eplus.module.scm.controller.admin.purchaseauxiliaryallocation.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
@Schema(description = "管理后台 - 采购合同辅料分摊 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PurchaseAuxiliaryAllocationRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "7679")
    @ExcelProperty("主键")
    private Long id;
    
    @Schema(description = "采购合同主键", example = "21266")
    @ExcelProperty("采购合同主键")
    private Long purchaseContractId;
    
    @Schema(description = "采购合同编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("采购合同编号")
    private String purchaseContractCode;
    
    @Schema(description = "采购合同明细主键", example = "20282")
    @ExcelProperty("采购合同明细主键")
    private Long purchaseContractItemId;
    
    @Schema(description = "产品编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("产品编号")
    private String skuCode;
    
    @Schema(description = "产品名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @ExcelProperty("产品名称")
    private String skuName;
    
    @Schema(description = "客户货号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("客户货号")
    private String cskuCode;
    
    @Schema(description = "辅料采购合同主键", example = "18880")
    @ExcelProperty("辅料采购合同主键")
    private Long auxiliaryPurchaseContractId;
    
    @Schema(description = "辅料采购合同编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("辅料采购合同编号")
    private String auxiliaryPurchaseContractCode;
    
    @Schema(description = "辅料采购合同明细主键", example = "9513")
    @ExcelProperty("辅料采购合同明细主键")
    private Long auxiliaryPurchaseContractItemId;
    
    @Schema(description = "辅料产品编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("辅料产品编号")
    private String auxiliarySkuCode;
    
    @Schema(description = "辅料产品名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
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