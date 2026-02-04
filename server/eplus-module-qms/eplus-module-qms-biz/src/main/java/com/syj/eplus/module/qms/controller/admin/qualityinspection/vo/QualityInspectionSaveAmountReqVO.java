package com.syj.eplus.module.qms.controller.admin.qualityinspection.vo;

import com.syj.eplus.framework.common.entity.JsonAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 验货单新增/修改 Request VO")
@Data
public class QualityInspectionSaveAmountReqVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "验货费用")
    private JsonAmount amount;

}
