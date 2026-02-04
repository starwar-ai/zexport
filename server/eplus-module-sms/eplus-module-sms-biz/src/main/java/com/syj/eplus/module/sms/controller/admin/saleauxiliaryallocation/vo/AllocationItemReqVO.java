package com.syj.eplus.module.sms.controller.admin.saleauxiliaryallocation.vo;

import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Schema(description = "管理后台 - 销售合同辅料分摊新增/修改 Request VO")
@Data
public class AllocationItemReqVO {

    @Schema(description = "销售合同明细主键", example = "29166")
    private Long saleContractItemId;


    @Schema(description = "分摊金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "分摊金额不能为空")
    private JsonAmount allocationAmount;

}