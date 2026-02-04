package com.syj.eplus.module.scm.controller.admin.venderbankaccount.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 供应商银行账户新增/修改 Request VO")
@Data
public class VenderBankaccountSaveReqVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "版本", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "版本不能为空")
    private Integer ver;

    @Schema(description = "供应商id", requiredMode = Schema.RequiredMode.REQUIRED, example = "17942")
    @NotNull(message = "供应商id不能为空")
    private Long venderId;

    @Schema(description = "供应商版本", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "供应商版本不能为空")
    private Integer venderVer;

    @Schema(description = "银行", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "银行不能为空")
    private String bank;

    @Schema(description = "银行账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "16402")
    @NotEmpty(message = "银行账号不能为空")
    private String bankAccount;

    @Schema(description = "是否默认账号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否默认账号不能为空")
    private Integer defaultFlag;

    @Schema(description = "开户行地址", requiredMode = Schema.RequiredMode.REQUIRED)
    private String bankAddress;

    @Schema(description = "开户行联系人", requiredMode = Schema.RequiredMode.REQUIRED)
    private String bankPoc;

    @Schema(description = "银行行号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String bankCode;

}