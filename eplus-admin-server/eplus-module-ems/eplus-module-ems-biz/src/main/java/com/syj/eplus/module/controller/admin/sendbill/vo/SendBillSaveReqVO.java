package com.syj.eplus.module.controller.admin.sendbill.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 寄件导入单据新增/修改 Request VO")
@Data
public class SendBillSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "20131")
    private Long id;
    @Schema(description = "序号")
    private Integer sortNum;

    @Schema(description = "运单号码")
    private String code;

    @Schema(description = "费用(元)")
    private BigDecimal cost;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "快递公司id")
    private Long venderId;

    @Schema(description = "快递公司编号")
    private String venderCode;

    @Schema(description = "快递公司名称")
    private String venderName;

    @Schema(description = "批次号")
    private String batchCode;

    @Schema(description = "日期")
    private String billDate;

}