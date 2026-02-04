package com.syj.eplus.module.infra.controller.admin.settlement.vo;

import com.syj.eplus.module.infra.api.settlement.dto.SystemCollectionPlanDTO;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 结汇方式 Response VO")
@Data
@ExcelIgnoreUnannotated
public class SettlementRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "10892")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "结汇名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @ExcelProperty("结汇名称")
    private String name;

    @Schema(description = "结汇英文名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("结汇英文名称")
    private String nameEng;

    @Schema(description = "起始日类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("起始日类型")
    private Integer dateType;

    @Schema(description = "天数", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("天数")
    private Integer duration;

    @Schema(description = "收款计划")
    private List<SystemCollectionPlanDTO> collectionPlanList;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}