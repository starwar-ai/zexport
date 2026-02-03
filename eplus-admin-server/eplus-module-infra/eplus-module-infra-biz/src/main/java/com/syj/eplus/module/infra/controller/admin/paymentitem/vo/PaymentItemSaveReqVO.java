package com.syj.eplus.module.infra.controller.admin.paymentitem.vo;

import com.syj.eplus.module.infra.api.paymentitem.dto.SystemPaymentPlanDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 付款方式新增/修改 Request VO")
@Data
public class PaymentItemSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "9949")
    private Long id;

    @Schema(description = "编号")
    private String code;

    @Schema(description = "名称", example = "李四")
    private String name;

    @Schema(description = "审核状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer auditStatus;

    @Schema(description = "起始日类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer dateType;

    @Schema(description = "天数")
    private Integer duration;

    @Schema(description = "付款计划")
    private List<SystemPaymentPlanDTO> systemPaymentPlanList;

}