package com.syj.eplus.module.crm.controller.admin.collectionaccount.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 收款账号新增/修改 Request VO")
@Data
public class CollectionAccountSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "21154")
    private Long id;
//
//    @Schema(description = "默认标记", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "默认标记不能为空")
//    private Integer defaultFlag;

    @Schema(description = "客户编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "客户编号不能为空")
    private String custCode;

    @Schema(description = "内部公司主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "24894")
    @NotNull(message = "内部公司主键不能为空")
    private Long companyId;

    @Schema(description = "内部公司银行账号主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "7066")
    @NotNull(message = "内部公司银行账号主键不能为空")
    private Long companyBankId;

}