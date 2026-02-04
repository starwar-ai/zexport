package com.syj.eplus.module.qms.controller.admin.qualityinspection.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Schema(description = "管理后台 - 验货单返工重验 Request VO")
@Data
@ToString(callSuper = true)
public class QualityInspectionReworkItemReqVO {

    @Schema(description = "返工说明")
    private String reworkDesc;

    @Schema(description = "明细主键")
    private Long itemId;


}
