package com.syj.eplus.module.sms.controller.admin.otherfee.vo;

import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Schema(description = "管理后台 - 其他费用新增/修改 Request VO")
@Data
public class OtherFeeSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "19294")
    private Long id;

    @Schema(description = "报价单id", example = "2927")
    private Long smsQuotationId;

    @Schema(description = "费用名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @NotEmpty(message = "费用名称不能为空")
    private String feeName;

    @Schema(description = "金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "金额不能为空")
    private JsonAmount amount;

}