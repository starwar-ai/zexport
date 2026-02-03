
package com.syj.eplus.module.scm.controller.admin.purchaseregistration.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/02/17:26
 * @Description:
 */
@Data
@ExcelIgnoreUnannotated
public class PurchaseRegistrationItemExportResp {
    @ExcelProperty("日期")
    private LocalDateTime invoicedDate;

    @ExcelProperty("供应商编号")
    private String venderCode;

    @ExcelProperty("供应商名称")
    private String venderName;

    @ExcelProperty("录入人")
    private String inputUserName;

    @ExcelProperty("录入日期")
    private LocalDateTime inputDate;

    @ExcelProperty("审核状态")
    private String auditStatusString;

    @ExcelProperty("币别")
    private String currency;

    @ExcelProperty("序号")
    private Integer sortNum;

    @ExcelProperty("发票单号")
    private String invoiceCode;

    @ExcelProperty("明细序号")
    private Integer childSortNum;

    @ExcelProperty("采购合同号")
    private String purchaseContractCode;

    @ExcelProperty("物料编号")
    private String skuCode;

    @ExcelProperty("发票数量")
    private Integer invoicNoticesQuantity;

    @ExcelProperty("发票单价")
    private BigDecimal invoicPrice;

    @ExcelProperty("发票金额")
    private BigDecimal invoiceAmount;

    @ExcelProperty("已开票金额")
    private BigDecimal invoicThisAmount;

    @ExcelProperty("开票数量")
    private Integer invoicThisQuantity;

    @ExcelProperty("开票单价")
    private BigDecimal invoicThisPrice;

    @ExcelProperty("税率")
    private BigDecimal taxRate;

    @ExcelProperty("税额")
    private BigDecimal taxAmount;

    @ExcelProperty("物料名称")
    private String invoicSkuName;
}
