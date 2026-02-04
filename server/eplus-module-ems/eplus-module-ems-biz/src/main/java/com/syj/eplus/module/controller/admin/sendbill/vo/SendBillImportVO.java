package com.syj.eplus.module.controller.admin.sendbill.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data

@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = false)
public class SendBillImportVO {

    @Schema(description = "快递公司")
    private String venderName;
    @Schema(description = "物流单号")
    private String emsCode;

    @Schema(description = "实际运费")
    private String costStr;

    @Schema(description = "录入人姓名")
    private String inputUserName;

    @Schema(description = "收件人信息")
    private String receiveMsg;
    @Schema(description = "费用归集")
    private String feeShare;
    @Schema(description = "编号")
    private String sendCode;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "寄件时间")
    private String month;

    @Schema(description = "异常原因")
    private String errorDesc;

    @Schema(description = "导入类型")
    private Integer importType;
    @Schema(description = "币种")
    private String currency;


    private String code;
}