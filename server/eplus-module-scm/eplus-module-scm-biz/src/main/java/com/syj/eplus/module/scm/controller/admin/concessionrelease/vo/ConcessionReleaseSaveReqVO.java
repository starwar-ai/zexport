package com.syj.eplus.module.scm.controller.admin.concessionrelease.vo;

import com.syj.eplus.framework.common.entity.SimpleFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "管理后台 - 让步放行新增/修改 Request VO")
@Data
public class ConcessionReleaseSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "7876")
    private Long id;

//    @Schema(description = "验货单主键 ", example = "30244")
//    private Long qualityInspectionId;

    @Schema(description = "验货单子表主键列表 ", example = "30244")
    private List<Long> qualityInspectionItemIdList;

    @Schema(description = "保函标记", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "保函标记不能为空")
    private Integer annexFlag;

    @Schema(description = "保函", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<SimpleFile>  factoryFile;

    @Schema(description = "图片", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<SimpleFile>  picture;

    @Schema(description = "审核状态", example = "2")
    private Integer auditStatus;

    @Schema(description = "让步描述", requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;

    @Schema(description = "提交审核标识")
    private Integer submitFlag;
}