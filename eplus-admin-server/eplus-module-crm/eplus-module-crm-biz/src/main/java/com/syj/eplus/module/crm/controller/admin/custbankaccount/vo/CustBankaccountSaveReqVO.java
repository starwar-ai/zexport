package com.syj.eplus.module.crm.controller.admin.custbankaccount.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 银行账户信息新增/修改 Request VO")
@Data
public class CustBankaccountSaveReqVO {


    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "开户行", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "开户行不能为空")
    private String bank;

    @Schema(description = "银行账户", requiredMode = Schema.RequiredMode.REQUIRED, example = "4349")
    @NotEmpty(message = "银行账户不能为空")
    private String bankAccount;

    @Schema(description = "是否默认账户0-否，1-是", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否默认账户0-否，1-是不能为空")
    private Integer defaultFlag;

    @Schema(description = "开户行地址")
    private String bankAddress;

    @Schema(description = "开户行联系人")
    private String bankPoc;

    @Schema(description = "银行账号")
    private String bankCode;
}