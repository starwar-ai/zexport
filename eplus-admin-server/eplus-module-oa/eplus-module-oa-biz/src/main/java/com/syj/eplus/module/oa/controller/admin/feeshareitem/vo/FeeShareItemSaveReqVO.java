package com.syj.eplus.module.oa.controller.admin.feeshareitem.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 费用归集明细新增/修改 Request VO")
@Data
public class FeeShareItemSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "31512")
    private Long id;

    @Schema(description = "费用归属id", example = "29806")
    private Long shareFeeId;

    @Schema(description = "费用归属对象类型", example = "1")
    private Integer businessSubjectType;

    @Schema(description = "费用归属对象编号")
    private Long businessSubjectCode;

    @Schema(description = "分摊比例")
    private Integer ratio;

}