
package com.syj.eplus.module.oa.controller.admin.paymentapp.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 公对公申请 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PaymentAppSendDetailRespVO {

    @Schema(description = "快递")
    @ExcelProperty("快递")
    private String expressCompany;

    @Schema(description = "面单号")
    @ExcelProperty("面单号")
    private String expressCode;

    @Schema(description = "价格")
    @ExcelProperty("价格")
    private String amount;

    @Schema(description = "经手人")
    @ExcelProperty("经手人")
    private String userName;

    @Schema(description = "实际寄件人")
    @ExcelProperty("实际寄件人")
    private String actualUserName;

    @Schema(description = "对方公司名称/收件人")
    @ExcelProperty("对方公司名称/收件人")
    private String receiveName;

    @Schema(description = "部门")
    @ExcelProperty("部门")
    private String deptName;

    @Schema(description = "订单号")
    @ExcelProperty("订单号")
    private String contractCodes;

    @Schema(description = "备注")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "月份")
    @ExcelProperty("月份")
    private String month;

}