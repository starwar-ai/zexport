package com.syj.eplus.module.qms.controller.admin.qualityinspection.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 验货单返工重验 Request VO")
@Data
@ToString(callSuper = true)
public class QualityInspectionReworkReqVO {

    @Schema(description = "重验时间")
    private LocalDateTime reworkInspectionTime;

    @Schema(description = "重验明细列表")
    private List<QualityInspectionReworkItemReqVO> itemList;
}
