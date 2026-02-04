package com.syj.eplus.module.scm.controller.admin.purchaseregistration.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.syj.eplus.framework.common.entity.SimpleFile;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.module.scm.dal.dataobject.purchaseregistrationitem.PurchaseRegistrationItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 采购跟单登记 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PurchaseRegistrationRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "7495")
    private Long id;

    @Schema(description = "编号")
    @ExcelProperty("发票登记编号")
    private String code;

    @Schema(description = "付款主体主键", example = "10997")
    private Long companyId;

    @Schema(description = "付款主体", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @ExcelProperty("付款主体")
    private String companyName;

    @Schema(description = "供应商编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("供应商编号")
    private String venderCode;

    @Schema(description = "供应商名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @ExcelProperty("供应商名称")
    private String venderName;

    @Schema(description = "发票类型", example = "1")
    private Integer taxType;

    @Schema(description = "发票编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("发票编号")
    private String invoiceCode;

    @Schema(description = "收票日期")
    private LocalDateTime invoicedDate;

    @Schema(description = "发票总金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("发票金额")
    private BigDecimal invoiceAmount;

    @Schema(description = "币别", requiredMode = Schema.RequiredMode.REQUIRED)
    private String currency;

    @Schema(description = "税率")
    private BigDecimal taxRate;

    @Schema(description = "附件", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<SimpleFile> annex;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "审核状态")
    private Integer auditStatus;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "录入人")
    private UserDept inputUser;

    @Schema(description = "录入日期")
    @ExcelProperty("登记日期")
    private LocalDateTime inputDate;

    @Schema(description = "流程实例编号")
    private String processInstanceId;

    @Schema(description = "采购跟单明细")
    private List<PurchaseRegistrationItem> children;

    @Schema(description = "复核日期")
    private LocalDateTime reviewDate;

    @Schema(description = "复核人")
    private UserDept reviewUser;
}