package com.syj.eplus.module.controller.admin.send.vo;

import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 寄件新增/修改 Request VO")
@Data
public class SendUpdateCostReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "25593")
    private Long id;

    @Schema(description = "实际费用")
    private JsonAmount cost;


}