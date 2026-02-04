package com.syj.eplus.module.oa.controller.admin.reimbshare.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 报销分摊新增/修改 Request VO")
@Data
public class ReimbShareSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "10146")
    private Long reimbShareId;

    @Schema(description = "报销单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "28790")
    @NotNull(message = "报销单编号不能为空")
    private Long reimbId;

    @Schema(description = "费用归属类型", example = "1")
    private Integer auxiliaryType;

    @Schema(description = "费用归属对象id", requiredMode = Schema.RequiredMode.REQUIRED, example = "18626")
    @NotNull(message = "费用归属对象id不能为空")
    private Long auxiliaryId;

    @Schema(description = "分摊比例", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "分摊比例不能为空")
    private BigDecimal ratio;

}