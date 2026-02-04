package com.syj.eplus.module.oa.controller.admin.subject.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
@Schema(description = "管理后台 - 科目 Response VO")
@Data
@ExcelIgnoreUnannotated
public class SubjectRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "14769")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "编号")
    @ExcelProperty("编号")
    private String code;
    
    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @ExcelProperty("名称")
    private String name;
    
    @Schema(description = "描述", example = "随便")
    @ExcelProperty("描述")
    private String description;
    
    @Schema(description = "层次")
    @ExcelProperty("层次")
    private Integer layer;
    
    @Schema(description = "科目性质")
    @ExcelProperty("科目性质")
    private Integer nature;
    
    @Schema(description = "科目类型", example = "2")
    @ExcelProperty("科目类型")
    private String type;
    
    @Schema(description = "辅助核算")
    @ExcelProperty("辅助核算")
    private String auxiliaryAccounting;
    
    @Schema(description = "核算编号")
    @ExcelProperty("核算编号")
    private String accountingNumber;
    
    @Schema(description = "是否外币核算")
    @ExcelProperty("是否外币核算")
    private Integer isForeignCurrencyAccounting;
    
    @Schema(description = "币种", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("币种")
    private String currency;
    
    @Schema(description = "是否期末调汇")
    @ExcelProperty("是否期末调汇")
    private Integer isFinalExchange;
    
    @Schema(description = "是否银行科目")
    @ExcelProperty("是否银行科目")
    private Integer isBank;
    
    @Schema(description = "是否现金科目")
    @ExcelProperty("是否现金科目")
    private Integer isCash;
    
    @Schema(description = "是否现金银行")
    @ExcelProperty("是否现金银行")
    private Integer isCashBank;
    
    @Schema(description = "父级科目id", example = "4249")
    @ExcelProperty("父级科目id")
    private Long parentId;
    
    @Schema(description = "父级科目名称", example = "张三")
    @ExcelProperty("父级科目名称")
    private String parentName;
    
    @Schema(description = "录入日期")
    @ExcelProperty("录入日期")
    private LocalDateTime inputDate;
    
    @Schema(description = "是否现金流量相关")
    @ExcelProperty("是否现金流量相关")
    private Integer isCashFlowRelated;
    
    @Schema(description = "是否末级")
    @ExcelProperty("是否末级")
    private Integer isLast;
    
    @Schema(description = "现金流量编号")
    @ExcelProperty("现金流量编号")
    private String cashFlowCode;
    
    @Schema(description = "现金流量名称", example = "赵六")
    @ExcelProperty("现金流量名称")
    private String cashFlowName;
    
    @Schema(description = "银行账户", requiredMode = Schema.RequiredMode.REQUIRED, example = "8840")
    @ExcelProperty("银行账户")
    private String bankAccount;
    
    @Schema(description = "银行账号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("银行账号")
    private String bankCode;
    
    @Schema(description = "余额方向")
    @ExcelProperty("余额方向")
    private Integer balanceDirection;
    
    @Schema(description = "科目余额")
    @ExcelProperty("科目余额")
    private Long balance;
    
}