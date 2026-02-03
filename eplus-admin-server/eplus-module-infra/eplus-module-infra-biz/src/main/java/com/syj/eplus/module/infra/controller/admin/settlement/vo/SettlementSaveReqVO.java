package com.syj.eplus.module.infra.controller.admin.settlement.vo;

import com.syj.eplus.module.infra.api.settlement.dto.SystemCollectionPlanDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "管理后台 - 结汇方式新增/修改 Request VO")
@Data
public class SettlementSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "10892")
    private Long id;

    @Schema(description = "结汇名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @NotNull(message = "结汇名称不能为空")
    private String name;

    @Schema(description = "结汇英文名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "结汇英文名称不能为空")
    private String nameEng;

    @Schema(description = "收款计划")
    private List<SystemCollectionPlanDTO> collectionPlanList;

    /**
     * 起始日类型
     */
    private Integer dateType;
    /**
     * 天数
     */
    private Integer duration;
}